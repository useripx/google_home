package com.googlehome.protect.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.googlehome.protect.model.Child
import com.googlehome.protect.model.LocationEntry
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboard(viewModel: ParentViewModel) {
    val childData by viewModel.childData.collectAsState()
    val trackingId by viewModel.trackingId.collectAsState()
    
    var showAddChildDialog by remember { mutableStateOf(false) }
    var inputId by remember { mutableStateOf("") }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Google Home Protect", modifier = Modifier.padding(16.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Divider()
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
            topBar = {
                Column {
                    // Tracking Status Bar
                    Surface(
                        color = Color(0xFF1A73E8),
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF89FA9B), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tracking Active - Real-time updates every 10s",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    CenterAlignedTopAppBar(
                        title = { Text("Google Home", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAddChildDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Child")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
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
                        label = { Text("Safety") }
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
                    if (trackingId == null) {
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
                                Text("ID: $trackingId", color = Color.DarkGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                                Button(onClick = { showAddChildDialog = true }, modifier = Modifier.padding(top = 16.dp)) {
                                    Text("Change ID")
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

            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                // Map Section
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(32.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false)
                    ) {
                        Marker(
                            state = MarkerState(position = currentLocation),
                            title = child.name.ifEmpty { "Child Device" },
                            snippet = "Battery: ${child.battery}%"
                        )
                    }
                }

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
                            Text(child.name.ifEmpty { "Child Device" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BatteryChargingFull, null, modifier = Modifier.size(14.dp), tint = Color(0xFF006E2C))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${child.battery}% • Connected", fontSize = 12.sp, color = Color.Gray)
                            }
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
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(historyList.take(10)) { entry ->
                        ActivityItem(entry)
                    }
                }
                    }
                }
                } // This brace closes the 0 -> branch
                1 -> {
                    // SAFETY SCREEN
                    Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Shield, null, modifier = Modifier.size(64.dp), tint = Color(0xFF1A73E8))
                            Spacer(Modifier.height(16.dp))
                            Text("Safety Features", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("Geo-fencing and SOS features coming soon.", color = Color.Gray)
                        }
                    }
                }
                2 -> {
                    // SETTINGS SCREEN
                    Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Settings, null, modifier = Modifier.size(64.dp), tint = Color(0xFF1A73E8))
                            Spacer(Modifier.height(16.dp))
                            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("Configure application and account settings here.", color = Color.Gray)
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
                    }
                }) {
                    Text("CONNECT")
                }
            }
        )
    }
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
