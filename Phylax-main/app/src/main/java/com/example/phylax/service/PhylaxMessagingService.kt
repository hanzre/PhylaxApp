package com.example.phylax.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.phylax.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PhylaxMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: "SOS Alert"
        val body = remoteMessage.notification?.body ?: "Someone needs help."
        val data = remoteMessage.data
        
        val userPhone = data["userPhone"] ?: ""
        val senderName = data["senderName"] ?: "Unknown"
        val latitude = data["latitude"]?.toDoubleOrNull() ?: 0.0
        val longitude = data["longitude"]?.toDoubleOrNull() ?: 0.0
        val sessionId = data["sessionId"] ?: ""
        
        showNotification(title, body, userPhone, senderName, latitude, longitude, sessionId)
    }

    private fun showNotification(
        title: String, 
        body: String, 
        userPhone: String, 
        senderName: String,
        latitude: Double,
        longitude: Double,
        sessionId: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("is_emergency_alert", true)
            putExtra("session_id", sessionId)
            putExtra("sender_name", senderName)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // "Call User" Action - only if phone number is available
        val callPendingIntent = if (userPhone.isNotEmpty()) {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$userPhone"))
            PendingIntent.getActivity(
                this, 1, callIntent, PendingIntent.FLAG_IMMUTABLE
            )
        } else null

        // "View Location" Action - opens Google Maps with the location
        val locationPendingIntent = if (latitude != 0.0 && longitude != 0.0) {
            val mapUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            PendingIntent.getActivity(
                this, 2, mapIntent, PendingIntent.FLAG_IMMUTABLE
            )
        } else null

        val channelId = "emergency_alerts"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText("$senderName: $body")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        callPendingIntent?.let { 
            notificationBuilder.addAction(android.R.drawable.ic_menu_call, "CALL USER", it) 
        }
        
        locationPendingIntent?.let { 
            notificationBuilder.addAction(android.R.drawable.ic_menu_mylocation, "VIEW LOCATION", it) 
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority emergency alerts"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to Firestore for the user
    }
}
