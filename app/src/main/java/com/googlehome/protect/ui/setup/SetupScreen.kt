package com.googlehome.protect.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.googlehome.protect.data.repository.FirebaseRepository
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.googlehome.protect.model.AppMode

@Composable
fun SetupScreen(
    onModeSelected: (AppMode) -> Unit,
    onParentActivated: (String) -> Unit // Pass parentId
) {
    var showActivationDialog by remember { mutableStateOf(false) }
    var activationCode by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val repository = remember { FirebaseRepository() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FF))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = Color(0xFF005BBF),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Google Home",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF191C23)
            )
        }
        
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .background(Color(0xFF86F898).copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "SECURE ENVIRONMENT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006E2C)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Pilih mode Anda",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191C23),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Untuk menjaga privasi, aplikasi ini akan tampil sebagai Google Home pada layar utama perangkat Anda.",
            fontSize = 14.sp,
            color = Color(0xFF414754),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Parent Mode Card
        ModeCard(
            title = "Mode Orang Tua",
            description = "Kelola perangkat, pantau lokasi secara real-time, dan terima peringatan keselamatan.",
            icon = Icons.Default.SupervisedUserCircle,
            color = Color(0xFF005BBF),
            onClick = { showActivationDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Kids Mode Card
        ModeCard(
            title = "Mode Anak",
            description = "Siapkan perangkat ini untuk dipantau. Proses latar belakang yang tersamar memastikan perlindungan.",
            icon = Icons.Default.ChildCare,
            color = Color(0xFF006E2C),
            onClick = { onModeSelected(AppMode.KIDS) }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "IDENTITY PROTECTION PROTOCOL: \"GOOGLE HOME\" UI SKIN ACTIVE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF727785),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }

    if (showActivationDialog) {
        AlertDialog(
            onDismissRequest = { if (!isVerifying) showActivationDialog = false },
            title = { Text("Aktivasi Mode Orang Tua") },
            text = {
                Column {
                    Text(
                        "Masukkan Kode Aktivasi unik yang Anda dapatkan dari administrator.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = activationCode,
                        onValueChange = { 
                            activationCode = it
                            errorMessage = null 
                        },
                        label = { Text("Kode Aktivasi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isVerifying
                    )
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    if (isVerifying) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            color = Color(0xFF005BBF)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (activationCode.isBlank()) {
                            errorMessage = "Kode tidak boleh kosong"
                            return@Button
                        }
                        isVerifying = true
                        scope.launch {
                            val parent = repository.verifyActivationCode(activationCode)
                            isVerifying = false
                            if (parent != null) {
                                onParentActivated(parent.id)
                                onModeSelected(AppMode.PARENT)
                                showActivationDialog = false
                            } else {
                                errorMessage = "Kode aktivasi tidak valid atau tidak ditemukan"
                            }
                        }
                    },
                    enabled = !isVerifying,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BBF))
                ) {
                    Text("VERIFIKASI")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showActivationDialog = false },
                    enabled = !isVerifying
                ) {
                    Text("BATAL")
                }
            }
        )
    }
}

@Composable
fun ModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191C23)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF414754),
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = title, fontWeight = FontWeight.Bold)
            }
        }
    }
}
