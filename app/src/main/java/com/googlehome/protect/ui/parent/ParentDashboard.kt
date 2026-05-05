package com.googlehome.protect.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.Heatmap
import com.googlehome.protect.model.Child
import com.googlehome.protect.model.LocationEntry
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.coroutines.launch
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import java.io.File
import java.io.FileOutputStream
import android.content.Intent
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboard(viewModel: ParentViewModel) {
    val childData by viewModel.childData.collectAsState()
    val trackingId by viewModel.trackingId.collectAsState()
    val trackedChildrenIds by viewModel.trackedChildrenIds.collectAsState()
    
    var showAddChildDialog by remember { mutableStateOf(false) }
    var inputId by remember { mutableStateOf("") }

    var showRenameDialog by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val trackingInterval by viewModel.trackingInterval.collectAsState()
    val powerSavingEnabled by viewModel.powerSavingEnabled.collectAsState()
    val geofenceRadius by viewModel.geofenceRadius.collectAsState()
    val geofenceEnabled by viewModel.geofenceEnabled.collectAsState()
    val etaMinutes by viewModel.etaMinutes.collectAsState()
    val parentId by viewModel.parentId.collectAsState()
    val homeLocation by viewModel.homeLocation.collectAsState()

    var showClearHistoryDialog by remember { mutableStateOf<String?>(null) }
    var showRemoveChildDialog by remember { mutableStateOf<String?>(null) }
    var showIntervalPicker by remember { mutableStateOf(false) }

    val batteryAlert by viewModel.batteryAlert.collectAsState()
    val anomalyAlert by viewModel.anomalyAlert.collectAsState()
    val emergencyAlert by viewModel.emergencyAlert.collectAsState()
    val emergencyAudio by viewModel.emergencyAudio.collectAsState()
    val geofenceAlert by viewModel.geofenceAlert.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(batteryAlert) {
        batteryAlert?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "OK")
            viewModel.dismissBatteryAlert()
        }
    }
    
    LaunchedEffect(anomalyAlert) {
        anomalyAlert?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "OK", duration = SnackbarDuration.Long)
            viewModel.dismissAnomalyAlert()
        }
    }

    LaunchedEffect(emergencyAlert) {
        emergencyAlert?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "SIAGA", duration = SnackbarDuration.Indefinite)
            viewModel.dismissEmergencyAlert()
        }
    }

    LaunchedEffect(geofenceAlert) {
        geofenceAlert?.let {
            snackbarHostState.showSnackbar(it, actionLabel = "OK")
            viewModel.dismissGeofenceAlert()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = selectedTab != 0,
        drawerContent = {
            ModalDrawerSheet {
                Text("Yogi Ario Protection v9.0", modifier = Modifier.padding(16.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { 
                            Text(
                                "Yogi Ario Smart Protection", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 18.sp
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAddChildDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Child")
                            }
                        },
                        // We use WindowInsets.None because the Column handles the status bar or the Scaffold does.
                        // Actually, if we want Google Home at the very top, we should handle insets here or in the Column.
                        windowInsets = TopAppBarDefaults.windowInsets
                    )

                    // Row 2: Tracking Status Bar (Pindah ke bawah header)
                    Surface(
                        color = Color(0xFF1A73E8),
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF89FA9B), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tracking Active - Real-time updates every 10s",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Row 3: Scrollable Chips
                    if (trackedChildrenIds.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(vertical = 4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(trackedChildrenIds.toList()) { id ->
                                val isActive = id == trackingId
                                FilterChip(
                                    selected = isActive,
                                    onClick = { viewModel.startTracking(id) },
                                    label = { 
                                        Text(
                                            if (isActive) {
                                                childData?.name?.ifEmpty { "Active Child" } ?: "Active Child"
                                            } else {
                                                id.take(6) + ".."
                                            }
                                        ) 
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1A73E8),
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.GridView, null) },
                        label = { Text("Devices") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Shield, null) },
                        label = { Text("Asisten AI") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { innerPadding ->
            when (selectedTab) {
                0 -> {
                    // MAPS/DEVICES SCREEN
                    if (trackingId == null && trackedChildrenIds.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No child device connected", color = Color.Gray)
                                Button(onClick = { showAddChildDialog = true }, modifier = Modifier.padding(top = 16.dp)) {
                                    Text("Add Child ID")
                                }
                            }
                        }
                    } else if (childData == null) {
                        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color(0xFF1A73E8))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Waiting for child device signal...", color = Color.Gray)
                                Text("ID: ${trackingId ?: "Unknown"}", color = Color.DarkGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                                
                                if (trackedChildrenIds.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text("Or select another child:", color = Color.Gray, fontSize = 12.sp)
                                    LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                                        items(trackedChildrenIds.toList()) { id ->
                                            FilterChip(
                                                selected = id == trackingId,
                                                onClick = { viewModel.startTracking(id) },
                                                label = { Text(id.take(8) + "...") },
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val child = childData!!
                        val currentLocation = LatLng(child.currentLat, child.currentLon)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
                        }

                        // Sync camera if location changes significantly
                        LaunchedEffect(currentLocation) {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(currentLocation))
                        }
                        
                        val context = LocalContext.current
                        val bottomSheetState = rememberStandardBottomSheetState(
                            initialValue = SheetValue.PartiallyExpanded
                        )
                        val scaffoldState = rememberBottomSheetScaffoldState(
                            bottomSheetState = bottomSheetState
                        )

                        BottomSheetScaffold(
                            modifier = Modifier.padding(innerPadding).fillMaxSize(),
                            scaffoldState = scaffoldState,
                            sheetPeekHeight = 140.dp,
                            sheetContainerColor = MaterialTheme.colorScheme.surface,
                            sheetContent = {
                                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                    // Status Card
                                    Card(
                                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F3FD)),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(24.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .background(Color(0xFFD8E2FF), RoundedCornerShape(16.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Smartphone, null, tint = Color(0xFF005BBF))
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(child.name.ifEmpty { "Child Device" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                                    IconButton(
                                                        onClick = {
                                                            inputName = child.name
                                                            showRenameDialog = true
                                                        },
                                                        modifier = Modifier.size(28.dp).padding(start = 4.dp)
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit Name", modifier = Modifier.size(16.dp), tint = Color.Gray)
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.BatteryChargingFull, null, modifier = Modifier.size(14.dp), tint = Color(0xFF006E2C))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("${child.battery}% • ${child.networkStatus}", fontSize = 12.sp, color = Color.Gray)
                                                }
                                            }
                                            Spacer(modifier = Modifier.weight(1f))
                                            IconButton(
                                                onClick = {
                                                    val uri = "google.navigation:q=${child.currentLat},${child.currentLon}"
                                                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
                                                    mapIntent.setPackage("com.google.android.apps.maps")
                                                    try {
                                                        context.startActivity(mapIntent)
                                                    } catch (e: Exception) {
                                                        val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://maps.google.com/?q=${child.currentLat},${child.currentLon}"))
                                                        context.startActivity(browserIntent)
                                                    }
                                                },
                                                modifier = Modifier.background(Color(0xFF1A73E8), CircleShape)
                                            ) {
                                                Icon(Icons.Default.Navigation, contentDescription = "Navigate", tint = Color.White)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    viewModel.triggerRemoteRing(child.id, true)
                                                },
                                                modifier = Modifier.background(Color(0xFFFF3B30), CircleShape)
                                            ) {
                                                Icon(Icons.Default.NotificationsActive, contentDescription = "Bunyikan Perangkat", tint = Color.White)
                                            }
                                        }
                                    }

                                    // Activity List
                                    Text(
                                        "Recent Activity",
                                        modifier = Modifier.padding(16.dp),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    
                                    val historyList = child.history.values.toList().sortedByDescending { it.timestamp }
                                    
                                    LazyColumn(
                                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                                        contentPadding = PaddingValues(bottom = 16.dp)
                                    ) {
                                        items(historyList.take(15)) { entry ->
                                            ActivityItem(entry)
                                        }
                                    }
                                }
                            }
                        ) { sheetPadding ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Fullscreen Map
                                GoogleMap(
                                    modifier = Modifier.fillMaxSize().padding(bottom = sheetPadding.calculateBottomPadding() / 2),
                                    cameraPositionState = cameraPositionState,
                                    uiSettings = MapUiSettings(zoomControlsEnabled = true, compassEnabled = true)
                                ) {
                                    Marker(
                                        state = MarkerState(position = currentLocation),
                                        title = child.name.ifEmpty { "Child Device" },
                                        snippet = "Battery: ${child.battery}%"
                                    )
                                    
                                    // Polyline for tracking history
                                    val historyPoints = child.history.values.toList()
                                        .sortedBy { it.timestamp }
                                        .takeLast(100) // Ambil 100 titik terakhir agar tidak lag
                                        .map { LatLng(it.latitude, it.longitude) }
                                    
                                    if (historyPoints.size > 1) {
                                        Polyline(
                                            points = historyPoints,
                                            color = Color(0xFFFF3B30), // Warna merah agar sangat jelas
                                            width = 15f,
                                            geodesic = true,
                                            zIndex = 1f
                                        )
                                        
                                        // Heatmap Layer (v9.0)
                                        Heatmap(
                                            data = historyPoints,
                                            radius = 50,
                                            opacity = 0.7f
                                        )

                                        // Marker untuk titik awal rute
                                        Marker(
                                            state = MarkerState(position = historyPoints.first()),
                                            title = "Titik Awal",
                                            icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN)
                                        )
                                    }
                                }

                                // ETA Widget
                                if (etaMinutes != null) {
                                    Card(
                                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Schedule, null, tint = Color(0xFF1A73E8), modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Tiba dalam ~$etaMinutes mnt", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }
                                
                                if (emergencyAudio != null) {
                                    Card(
                                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp).clickable {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(emergencyAudio))
                                            context.startActivity(intent)
                                        },
                                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Mic, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Rekaman Darurat", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // ASISTEN AI SCREEN (v9.0)
                    AssistantChatScreen(modifier = Modifier.padding(innerPadding))
                }
                2 -> {
                    // SETTINGS SCREEN v3.0
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("ID Orang Tua (Parent ID)", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(parentId ?: "Generating...", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = 2.sp)
                                    Text("Gunakan ID ini untuk menautkan perangkat anak.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text("Manajemen Perangkat", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8), modifier = Modifier.padding(vertical = 8.dp))
                        }
                        
                        items(trackedChildrenIds.toList()) { id ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(if (id == trackingId) childData?.name?.ifEmpty { id } ?: id else id, fontWeight = FontWeight.Bold)
                                        Text("ID: $id", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { showRemoveChildDialog = id }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = { showAddChildDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Tambah Anak Baru")
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            Text("Pengaturan Tracking", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8), modifier = Modifier.padding(vertical = 8.dp))
                        }

                        item {
                            SettingsItem(
                                title = "Interval Update",
                                subtitle = "Update setiap ${trackingInterval / 1000} detik",
                                icon = Icons.Default.Timer,
                                onClick = { showIntervalPicker = true }
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Mode Hemat Daya Otomatis", fontWeight = FontWeight.Medium)
                                    Text("Perlambat update saat baterai anak < 20%", fontSize = 12.sp, color = Color.Gray)
                                }
                                Switch(checked = powerSavingEnabled, onCheckedChange = { viewModel.setPowerSavingMode(it) })
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                            Text("Geofencing (Area Aman)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8), modifier = Modifier.padding(vertical = 8.dp))
                        }

                        item {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text("Aktifkan Geofencing", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                                Switch(checked = geofenceEnabled, onCheckedChange = { viewModel.setGeofenceEnabled(it) })
                            }
                            
                            Text("Radius Aman: ${geofenceRadius.toInt()} meter", fontSize = 14.sp)
                            Slider(
                                value = geofenceRadius,
                                onValueChange = { viewModel.setGeofenceRadius(it) },
                                valueRange = 100f..2000f,
                                steps = 19
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            Text("Lokasi Rumah (ETA)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8), modifier = Modifier.padding(vertical = 8.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        if (homeLocation != null) "Lokasi Terdaftar: ${String.format("%.4f", homeLocation!!.first)}, ${String.format("%.4f", homeLocation!!.second)}" 
                                        else "Belum ada lokasi rumah",
                                        fontSize = 14.sp
                                    )
                                    Button(
                                        onClick = { 
                                            childData?.let { 
                                                viewModel.setHomeLocation(it.currentLat, it.currentLon)
                                            }
                                        },
                                        modifier = Modifier.padding(top = 8.dp),
                                        enabled = childData != null
                                    ) {
                                        Text("Set Lokasi Anak Saat Ini Sebagai Rumah")
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                            Text("Keamanan & Data", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8), modifier = Modifier.padding(vertical = 8.dp))
                        }

                        item {
                            OutlinedButton(
                                onClick = { trackingId?.let { showClearHistoryDialog = it } },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                            ) {
                                Text("Hapus Riwayat Lokasi")
                            }

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Button(
                                    onClick = { exportToCsv(context, childData) },
                                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Ekspor CSV")
                                }
                                Button(
                                    onClick = { exportToPdf(context, childData) },
                                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                                ) {
                                    Text("Ekspor PDF")
                                }
                            }

                            Spacer(Modifier.height(32.dp))
                            Spacer(Modifier.height(32.dp))
                            
                            // GEOFENCING CONFIG
                            Text("Pengaturan Keamanan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A73E8), modifier = Modifier.padding(vertical = 8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Batas Aman (Geofencing)", fontWeight = FontWeight.Bold)
                                    Text("Fitur ini mengatur radius batas aman lokasi anak. Konfigurasi lebih lanjut sedang dikembangkan.", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            
                            Spacer(Modifier.height(32.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Yogi Ario Protection v9.0", color = Color.Gray, fontSize = 12.sp)
                                Text("Status Firebase: Connected", color = Color(0xFF4CAF50), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddChildDialog) {
        AlertDialog(
            onDismissRequest = { showAddChildDialog = false },
            title = { Text("Connect Child Device") },
            text = {
                OutlinedTextField(
                    value = inputId,
                    onValueChange = { inputId = it },
                    label = { Text("Enter Child ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (inputId.isNotBlank()) {
                        viewModel.startTracking(inputId)
                        showAddChildDialog = false
                        showRenameDialog = true
                        inputName = ""
                        // Don't clear inputId yet so we know who we just added if needed
                    }
                }) {
                    Text("CONNECT")
                }
            }
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Set Child Name") },
            text = {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Enter Child Name") },
                    placeholder = { Text("e.g. John's Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (inputName.isNotBlank() && trackingId != null) {
                        viewModel.updateChildName(trackingId!!, inputName)
                    }
                    showRenameDialog = false
                    inputId = "" // clear it now
                }) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRenameDialog = false
                    inputId = ""
                }) {
                    Text("SKIP")
                }
            }
        )
    }
    if (showClearHistoryDialog != null) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = null },
            title = { Text("Hapus Riwayat?") },
            text = { Text("Semua data perjalanan untuk perangkat ini akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = { 
                    showClearHistoryDialog?.let { viewModel.clearHistory(it) }
                    showClearHistoryDialog = null
                }) { Text("HAPUS", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = null }) { Text("BATAL") }
            }
        )
    }

    if (showRemoveChildDialog != null) {
        AlertDialog(
            onDismissRequest = { showRemoveChildDialog = null },
            title = { Text("Hapus Perangkat?") },
            text = { 
                Text("Apakah anda yakin ingin menghapus ${if (showRemoveChildDialog == trackingId) childData?.name ?: showRemoveChildDialog else showRemoveChildDialog}?") 
            },
            confirmButton = {
                TextButton(onClick = { 
                    showRemoveChildDialog?.let { viewModel.removeChild(it) }
                    showRemoveChildDialog = null
                }) { Text("HAPUS", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveChildDialog = null }) { Text("BATAL") }
            }
        )
    }

    if (showIntervalPicker) {
        AlertDialog(
            onDismissRequest = { showIntervalPicker = false },
            title = { Text("Pilih Interval Update") },
            text = {
                Column {
                    listOf(10000L, 30000L, 60000L, 300000L).forEach { interval ->
                        Row(
                            Modifier.fillMaxWidth().clickable { 
                                viewModel.setTrackingInterval(interval)
                                showIntervalPicker = false
                            }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = trackingInterval == interval, onClick = null)
                            Spacer(Modifier.width(16.dp))
                            Text(when(interval) {
                                10000L -> "10 Detik"
                                30000L -> "30 Detik"
                                60000L -> "1 Menit"
                                else -> "5 Menit"
                            })
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

private fun exportToCsv(context: android.content.Context, child: Child?) {
    if (child == null) return
    val historyList = child.history.values.toList().sortedBy { it.timestamp }
    val csvContent = StringBuilder("Timestamp,Latitude,Longitude,Battery\n")
    historyList.forEach { entry ->
        csvContent.append("${entry.timestamp},${entry.latitude},${entry.longitude},${entry.battery}\n")
    }
    
    val file = File(context.cacheDir, "history_${child.name}.csv")
    FileOutputStream(file).use { it.write(csvContent.toString().toByteArray()) }
    shareFile(context, file, "text/csv")
}

private fun exportToPdf(context: android.content.Context, child: Child?) {
    if (child == null) return
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()
    
    paint.textSize = 18f
    paint.isFakeBoldText = true
    canvas.drawText("Laporan Riwayat Lokasi: ${child.name}", 50f, 50f, paint)
    
    paint.textSize = 12f
    paint.isFakeBoldText = false
    var y = 80f
    canvas.drawText("ID Perangkat: ${child.id}", 50f, y, paint)
    y += 20f
    canvas.drawText("Total Titik: ${child.history.size}", 50f, y, paint)
    y += 40f
    
    canvas.drawText("Waktu, Lat, Lon, Baterai", 50f, y, paint)
    y += 20f
    
    val historyList = child.history.values.toList().sortedByDescending { it.timestamp }.take(25)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    historyList.forEach { entry ->
        if (y > 800f) return@forEach // Simple page overflow handling
        val text = "${sdf.format(Date(entry.timestamp))}, ${entry.latitude}, ${entry.longitude}, ${entry.battery}%"
        canvas.drawText(text, 50f, y, paint)
        y += 15f
    }
    
    pdfDocument.finishPage(page)
    val file = File(context.cacheDir, "report_${child.name}.pdf")
    FileOutputStream(file).use { pdfDocument.writeTo(it) }
    pdfDocument.close()
    shareFile(context, file, "application/pdf")
}

private fun shareFile(context: android.content.Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Bagikan File"))
}

@Composable
fun ActivityItem(entry: LocationEntry) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFFecedf7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color(0xFF414754))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Location Update", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                SimpleDateFormat("HH:mm:ss dd MMM", Locale.getDefault()).format(Date(entry.timestamp)),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
