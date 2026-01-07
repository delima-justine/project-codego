package com.example.project_codego

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_codego.viewmodel.AuthViewModel
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class UserLocation(
    val userId: String = "",
    val userName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Initialize OSM Configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_config", MODE_PRIVATE))
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0088CC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (!hasLocationPermission) {
                PermissionRequestScreen {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                TrackerMapContent(
                    currentUserEmail = currentUser?.email ?: "Unknown",
                    currentUserId = currentUser?.uid ?: ""
                )
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF0088CC)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enable Location Tracking",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "To see your friends and share your location, we need access to your device's location.",
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC))
        ) {
            Text("Allow Location Access")
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun TrackerMapContent(
    currentUserEmail: String,
    currentUserId: String
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var otherLocations by remember { mutableStateOf<List<UserLocation>>(emptyList()) }
    var isSharing by remember { mutableStateOf(false) }
    var myCurrentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    
    // Debug Status State
    var statusMessage by remember { mutableStateOf("Ready to share") }

    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    fun getColorForUser(userId: String): Int {
        val hash = userId.hashCode()
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        return android.graphics.Color.rgb(r, g, b)
    }

    fun createColoredMarker(color: Int): Drawable {
        val size = 48
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = color
        paint.isAntiAlias = true
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(size / 2f, size / 2f, size / 4f, paint)
        return BitmapDrawable(context.resources, bitmap)
    }

    DisposableEffect(isSharing) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->  
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    myCurrentLocation = geoPoint
                    statusMessage = "GPS Locked. Uploading..."
                    
                    if (currentUserId.isNotEmpty()) {
                        val userLocation = UserLocation(
                            userId = currentUserId,
                            userName = currentUserEmail.substringBefore("@"),
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timestamp = System.currentTimeMillis()
                        )
                        firestore.collection("location_sharing")
                            .document(currentUserId)
                            .set(userLocation)
                            .addOnSuccessListener {
                                statusMessage = "Location Synced (Live)"
                            }
                            .addOnFailureListener { e ->
                                statusMessage = "Upload Failed: ${e.message}"
                            }
                    }
                    
                    mapViewRef.value?.let { map ->
                        if (map.zoomLevelDouble < 10) {
                            map.controller.setZoom(18.0)
                            map.controller.setCenter(geoPoint)
                        }
                    }
                }
            }
        }

        if (isSharing) {
            statusMessage = "Requesting Location..."
            // Use BALANCED_POWER_ACCURACY for better indoor results
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            statusMessage = "Sharing Paused"
            myCurrentLocation = null
            // Remove from Firebase so others can't see us
            if (currentUserId.isNotEmpty()) {
                firestore.collection("location_sharing")
                    .document(currentUserId)
                    .delete()
            }
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            // Cleanup on exit
            if (currentUserId.isNotEmpty()) {
                firestore.collection("location_sharing")
                    .document(currentUserId)
                    .delete()
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = firestore.collection("location_sharing")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    statusMessage = "Download Error: ${e.message}"
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val locations = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(UserLocation::class.java)
                    }.filter { it.userId != currentUserId }
                    otherLocations = locations
                }
            }
        onDispose { listener.remove() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(6.0)
                    controller.setCenter(GeoPoint(12.8797, 121.7740)) // Center of Philippines
                    mapViewRef.value = this
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { map ->
                map.overlays.clear()

                otherLocations.forEach { user ->
                    val userMarker = Marker(map)
                    userMarker.position = GeoPoint(user.latitude, user.longitude)
                    userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    userMarker.title = user.userName
                    userMarker.subDescription = "Live"
                    val userColor = getColorForUser(user.userId)
                    userMarker.icon = createColoredMarker(userColor)
                    map.overlays.add(userMarker)
                }

                myCurrentLocation?.let { loc ->
                    val myMarker = Marker(map)
                    myMarker.position = loc
                    myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    myMarker.title = "Me"
                    myMarker.subDescription = "My Location"
                    myMarker.icon = createColoredMarker(android.graphics.Color.BLUE)
                    map.overlays.add(myMarker)
                }
                
                map.invalidate()
            }
        )

        // Status Overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
        ) {
            Text(
                text = statusMessage,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // Active Users List
        if (otherLocations.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .padding(top = 30.dp) // Push down below status
                    .widthIn(max = 200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Active Users (${otherLocations.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(otherLocations) { user ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(8.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(getColorForUser(user.userId))
                                ) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(user.userName, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isSharing) "Sharing Location" else "Location Hidden",
                        fontWeight = FontWeight.Bold,
                        color = if (isSharing) Color(0xFF4CAF50) else Color.Gray
                    )
                    Text(
                        text = if (isSharing) "Others can see you" else "You are invisible",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = isSharing,
                    onCheckedChange = { isSharing = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50)
                    )
                )
            }
        }
    }
}