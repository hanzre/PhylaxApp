package com.example.phylax.ui.screens.emergency

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.location.Location
import android.net.Uri
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.phylax.ui.theme.Background
import com.example.phylax.ui.theme.LocationBlue
import com.example.phylax.ui.theme.PrimaryText
import com.example.phylax.ui.theme.SecondaryText
import com.example.phylax.ui.theme.iOSBlue
import com.example.phylax.ui.theme.iOSGreen
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import kotlin.math.roundToInt

private const val MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/positron"
private const val USER_SOURCE_ID = "phylax-user-location"
private const val ACCURACY_SOURCE_ID = "phylax-location-accuracy"
private const val ACCURACY_LAYER_ID = "phylax-location-accuracy-layer"
private const val USER_LAYER_ID = "phylax-user-location-layer"

private data class MapLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float
)

@SuppressLint("MissingPermission")
@Composable
fun LiveLocationScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission =
            result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    if (!hasLocationPermission) {
        LocationPermissionContent(
            onAllow = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
        return
    }

    var mapLocation by remember { mutableStateOf<MapLocation?>(null) }
    var hasCentered by remember { mutableStateOf(false) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }
    var mapLoadTimedOut by remember { mutableStateOf(false) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    mapLocation = MapLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy
                    )
                }
            }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@LaunchedEffect

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).setMinUpdateIntervalMillis(1000L).build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            // Use a cached fix immediately, then request a fresh high-accuracy fix.
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    mapLocation = MapLocation(it.latitude, it.longitude, it.accuracy)
                }
            }
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        mapLocation = MapLocation(it.latitude, it.longitude, it.accuracy)
                    }
                }
        } catch (e: SecurityException) {
            hasLocationPermission = false
        }
    }

    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).apply {
            onCreate(null)
        }
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            map.uiSettings.setLogoEnabled(false)
            map.uiSettings.setAttributionEnabled(false)
            map.uiSettings.setCompassEnabled(false)
            mapLibreMap = map

            map.setStyle(Style.Builder().fromUri(MAP_STYLE_URL)) { style ->
                style.addSource(
                    GeoJsonSource(
                        USER_SOURCE_ID,
                        FeatureCollection.fromFeatures(emptyList<Feature>())
                    )
                )
                style.addSource(
                    GeoJsonSource(
                        ACCURACY_SOURCE_ID,
                        FeatureCollection.fromFeatures(emptyList<Feature>())
                    )
                )

                style.addLayer(
                    FillLayer(ACCURACY_LAYER_ID, ACCURACY_SOURCE_ID).withProperties(
                        PropertyFactory.fillColor(AndroidColor.argb(38, 10, 132, 255)),
                        PropertyFactory.fillOutlineColor(AndroidColor.argb(110, 10, 132, 255))
                    )
                )
                style.addLayer(
                    CircleLayer(USER_LAYER_ID, USER_SOURCE_ID).withProperties(
                        PropertyFactory.circleColor(AndroidColor.rgb(10, 132, 255)),
                        PropertyFactory.circleRadius(7f),
                        PropertyFactory.circleStrokeWidth(2.5f),
                        PropertyFactory.circleStrokeColor(AndroidColor.WHITE)
                    )
                )

                mapStyle = style
                mapLoadTimedOut = false
            }
        }
    }

    LaunchedEffect(mapStyle) {
        if (mapStyle != null) {
            mapLoadTimedOut = false
            return@LaunchedEffect
        }
        delay(20_000)
        if (mapStyle == null) {
            mapLoadTimedOut = true
        }
    }

    LaunchedEffect(mapLocation, mapStyle) {
        val location = mapLocation ?: return@LaunchedEffect
        val style = mapStyle ?: return@LaunchedEffect

        val userPoint = Point.fromLngLat(location.longitude, location.latitude)
        style.getSourceAs<GeoJsonSource>(USER_SOURCE_ID)?.setGeoJson(
            FeatureCollection.fromFeature(Feature.fromGeometry(userPoint))
        )

        val accuracyShape = buildAccuracyPolygon(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = location.accuracyMeters.toDouble()
        )
        style.getSourceAs<GeoJsonSource>(ACCURACY_SOURCE_ID)?.setGeoJson(
            FeatureCollection.fromFeature(Feature.fromGeometry(accuracyShape))
        )

        if (!hasCentered) {
            mapLibreMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    17.0
                ),
                700
            )
            hasCentered = true
        }
    }

    DisposableEffect(mapView) {
        mapView.onStart()
        mapView.onResume()
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    val currentLocation = mapLocation
    val isMapReady = mapStyle != null

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        if (!isMapReady || currentLocation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!mapLoadTimedOut) {
                        CircularProgressIndicator(color = LocationBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = when {
                            mapLoadTimedOut -> "Map could not load"
                            !isMapReady -> "Loading map…"
                            else -> "Getting your location…"
                        },
                        color = PrimaryText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            mapLoadTimedOut -> "Check your internet connection, then reopen this screen."
                            !isMapReady -> "Fetching OpenFreeMap map tiles"
                            else -> "Make sure GPS is turned on"
                        },
                        color = SecondaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Floating header
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .height(44.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color.White.copy(alpha = 0.9f),
            border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(iOSGreen, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LIVE LOCATION",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Floating back button
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
                .size(44.dp)
                .align(Alignment.TopStart),
            shape = CircleShape,
            color = Color.White,
            onClick = onBack,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = iOSBlue)
            }
        }

        // Bottom status card
        Surface(
            modifier = Modifier
                .padding(20.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f)),
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = iOSGreen.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = iOSGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current Status",
                            style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText)
                        )
                        Text(
                            text = if (currentLocation != null) "Tracking active" else "Locating…",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        if (currentLocation != null && currentLocation.accuracyMeters > 0f) {
                            Text(
                                text = "Accuracy ±${currentLocation.accuracyMeters.roundToInt()} m",
                                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            currentLocation?.let { location ->
                                mapLibreMap?.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(location.latitude, location.longitude),
                                        17.0
                                    ),
                                    500
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Recenter map",
                            tint = iOSBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        currentLocation?.let { location ->
                            val geoUri = Uri.parse(
                                "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
                            )
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                            } catch (_: Exception) {
                                // No maps app available; stay on the in-app map.
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = iOSBlue),
                    enabled = currentLocation != null
                ) {
                    Text(
                        if (currentLocation != null) "Verify in Full Maps" else "Getting Location...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Text(
                    text = "© OpenStreetMap contributors · Tiles by OpenFreeMap",
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryText.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

private fun buildAccuracyPolygon(
    latitude: Double,
    longitude: Double,
    accuracyMeters: Double
): Polygon {
    val earthRadiusMeters = 6_371_000.0
    val distance = accuracyMeters.coerceIn(1.0, 5_000.0)
    val angularDistance = distance / earthRadiusMeters
    val latRad = Math.toRadians(latitude)
    val lonRad = Math.toRadians(longitude)

    val points = ArrayList<Point>(65)
    for (i in 0 until 64) {
        val bearing = Math.toRadians(i * 360.0 / 64.0)
        val lat2 = Math.asin(
            Math.sin(latRad) * Math.cos(angularDistance) +
                Math.cos(latRad) * Math.sin(angularDistance) * Math.cos(bearing)
        )
        val lon2 = lonRad + Math.atan2(
            Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(latRad),
            Math.cos(angularDistance) - Math.sin(latRad) * Math.sin(lat2)
        )
        points.add(Point.fromLngLat(Math.toDegrees(lon2), Math.toDegrees(lat2)))
    }
    points.add(points.first())

    return Polygon.fromLngLats(listOf(points))
}

@Composable
private fun LocationPermissionContent(onAllow: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(28.dp),
            color = LocationBlue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = LocationBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Location Permission Needed",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = PrimaryText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Phylax uses your precise location to show you on the map and to share it with your emergency contacts during an SOS.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = SecondaryText,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAllow,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = iOSBlue)
        ) {
            Text("ALLOW LOCATION", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "If the prompt doesn't appear, enable Location for Phylax in Android Settings.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SecondaryText,
                textAlign = TextAlign.Center
            )
        )
    }
}
