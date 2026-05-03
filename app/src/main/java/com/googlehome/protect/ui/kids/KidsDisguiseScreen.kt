package com.googlehome.protect.ui.kids

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun KidsDisguiseScreen(childId: String?, lastUpdateDate: Long) {
    var showId by remember { mutableStateOf(false) }

    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val lastUpdateStr = if (lastUpdateDate > 0) formatter.format(Date(lastUpdateDate)) else "Calculating..."
    val securityUpdateStr = if (lastUpdateDate > 0) formatter.format(Date(lastUpdateDate - (5L * 24 * 60 * 60 * 1000))) else "Calculating..."
    val androidVersion = Build.VERSION.RELEASE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FF))
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        // Status Bar Space
        Spacer(modifier = Modifier.height(30.dp))

        // Header with 10-Second Easter Egg
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val upOrCancel = withTimeoutOrNull(10000L) {
                            waitForUpOrCancellation()
                        }
                        if (upOrCancel == null) {
                            // Timeout reached without lifting finger - 10 seconds!
                            showId = true
                        }
                    }
                }
        ) {
            Text(
                text = "System Update",
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF27343F),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Checking for updates...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF53606D)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Progress Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            LinearProgressIndicator(
                progress = { 0.94f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = Color(0xFF005AC1),
                trackColor = Color(0xFFD6E4F3),
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "94% COMPLETE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF53606D),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "v14.2.0_build_99",
                    fontSize = 10.sp,
                    color = Color(0xFF6F7C89),
                    fontWeight = FontWeight.Light
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Technical Metadata
        MetadataItem(label = "Last successful update", value = lastUpdateStr)
        Spacer(modifier = Modifier.height(32.dp))
        MetadataItem(label = "Android version", value = androidVersion)
        Spacer(modifier = Modifier.height(32.dp))
        MetadataItem(label = "Security update", value = securityUpdateStr)

        Spacer(modifier = Modifier.weight(1f))

        // Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4FC)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF005AC1),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Your device is currently finalizing the latest system patches. This process may take a few minutes.",
                        fontSize = 14.sp,
                        color = Color(0xFF53606D),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keep your device connected to Wi-Fi for optimal performance.",
                        fontSize = 12.sp,
                        color = Color(0xFF6F7C89)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Invisible Child ID Overlay (Easter Egg Result)
        if (showId) {
            AlertDialog(
                onDismissRequest = { showId = false },
                title = { Text("Device Identity") },
                text = { 
                    Column {
                        Text("This ID is for Parent connection:")
                        Text(
                            text = childId ?: "GEN-ERROR",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005AC1),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showId = false }) {
                        Text("CLOSE")
                    }
                }
            )
        }

        // Ghost Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "CHECK FOR UPDATES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF005AC1),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* No-op for disguise */ }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun MetadataItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF53606D),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF27343F)
        )
    }
}
