package com.example.phylax.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.phylax.MainActivity
import com.example.phylax.data.repository.EmergencyRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.*

class SOSService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var mediaPlayer: MediaPlayer? = null
    
    private val emergencyRepository = EmergencyRepository()
    private var currentSessionId: String? = null

    companion object {
        private const val CHANNEL_ID = "SOS_SERVICE_CHANNEL"
        private const val NOTIFICATION_ID = 1001
        
        const val ACTION_START_SOS = "ACTION_START_SOS"
        const val ACTION_STOP_SOS = "ACTION_STOP_SOS"
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SOS -> {
                val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
                startSOS(userId)
            }
            ACTION_STOP_SOS -> {
                stopSOS()
            }
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startSOS(userId: String) {
        val notification = createNotification("Phylax SOS is Active", "Your location is being shared.")
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.e("SOSService", "Unable to start foreground SOS service: ${e.message}")
            stopSelf()
            return
        }

        serviceScope.launch {
            // Start emergency session in Firestore
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                serviceScope.launch {
                    val result = emergencyRepository.startEmergencySession(
                        userId, 
                        location?.latitude ?: 0.0, 
                        location?.longitude ?: 0.0
                    )
                    currentSessionId = result.getOrNull()
                    
                    // Start periodic updates
                    startLocationUpdates()

                    // Send SMS to emergency contacts
                    sendEmergencySms(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                }
            }
        }

        playAlarm()
    }

    private fun sendEmergencySms(lat: Double, lon: Double) {
        serviceScope.launch {
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            emergencyRepository.getContacts(userId).onSuccess { contacts ->
                val primaryContact = contacts.find { it.isPrimary } ?: contacts.firstOrNull()
                primaryContact?.let { contact ->
                    val message = "🚨 PHYLAX SOS ALERT!\n" +
                                  "I need help. My current location is:\n" +
                                  "https://www.google.com/maps/search/?api=1&query=$lat,$lon"
                  
                    try {
                        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            getSystemService(SmsManager::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            SmsManager.getDefault()
                        }
                        
                        // Convert PH number 09... to +63... for the carrier
                        val smsNumber = if (contact.phone.startsWith("0")) "+63" + contact.phone.substring(1) else contact.phone
                        
                        smsManager.sendTextMessage(smsNumber, null, message, null, null)
                        Log.d("SOSService", "SMS sent to ${contact.name}")
                    } catch (e: Exception) {
                        Log.e("SOSService", "Failed to send SMS: ${e.message}")
                    }
                }
            }
        }
    }

    private fun stopSOS() {
        serviceScope.launch {
            currentSessionId?.let { emergencyRepository.endEmergencySession(it) }
            stopLocationUpdates()
            stopAlarm()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    serviceScope.launch {
                        currentSessionId?.let { 
                            emergencyRepository.updateLocation(it, location.latitude, location.longitude)
                        }
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }
        locationCallback = null
    }

    private fun playAlarm() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            mediaPlayer = MediaPlayer.create(this, alarmUri)?.apply {
                isLooping = true
                start()
            }
            if (mediaPlayer == null) {
                Log.e("SOSService", "Unable to create alarm MediaPlayer")
            }
        } catch (e: Exception) {
            Log.e("SOSService", "Failed to play alarm: ${e.message}")
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, SOSService::class.java).apply {
            action = ACTION_STOP_SOS
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP SOS", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SOS Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        stopLocationUpdates()
        stopAlarm()
    }
}
