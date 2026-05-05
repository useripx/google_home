package com.googlehome.protect.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.googlehome.protect.api.GroqApiClient
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*

class AssistantViewModel : ViewModel() {
    private val groqClient = GroqApiClient()
    
    val messages = mutableStateListOf<Pair<String, String>>() // Pair<Role, Content>, Role: "user" | "assistant"
    var isLoading by mutableStateOf(false)
        private set

    init {
        messages.add("assistant" to "Hai, saya Yogi Ario Assisten AI aplikasi smart Protection ada yang bisa dibantu hari ini?")
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages.add("user" to text)
        isLoading = true

        viewModelScope.launch {
            // prepare history: Pair<User, Assistant>
            val historyPairs = mutableListOf<Pair<String, String>>()
            var lastUserMsg = ""
            for (msg in messages.drop(1)) { // skip first greeting
                if (msg.first == "user") {
                    lastUserMsg = msg.second
                } else if (msg.first == "assistant") {
                    if (lastUserMsg.isNotEmpty()) {
                        historyPairs.add(lastUserMsg to msg.second)
                        lastUserMsg = ""
                    }
                }
            }

            val response = groqClient.sendMessage(text, historyPairs)
            kotlinx.coroutines.delay(5000) // Jeda 5 detik agar lebih manusiawi
            messages.add("assistant" to response)
            isLoading = false
        }
    }

    fun clearChat() {
        messages.clear()
        messages.add("assistant" to "Hai, saya Yogi Ario Assisten AI aplikasi smart Protection ada yang bisa dibantu hari ini?")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantChatScreen(modifier: Modifier = Modifier, viewModel: AssistantViewModel = viewModel()) {
    var inputText by remember { mutableStateOf("") }
    
    val templates = listOf(
        "Bagaimana cara kerja Mode Kalkulator?",
        "Cara mengatur batas aman Geofence?",
        "Mengapa ETA tidak muncul?",
        "Bagaimana Cara Install Mode Anak beserta cara settingnya?"
    )

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Chat Messages
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true
        ) {
            items(viewModel.messages.reversed()) { msg ->
                val isUser = msg.first == "user"
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(
                                topStart = 16.dp, 
                                topEnd = 16.dp, 
                                bottomStart = if (isUser) 16.dp else 4.dp, 
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ))
                            .background(if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.second,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }


        if (viewModel.isLoading) {
            val infiniteTransition = rememberInfiniteTransition()
            val dotCount by infiniteTransition.animateValue(
                initialValue = 0,
                targetValue = 4,
                typeConverter = Int.VectorConverter,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Yogi Ario sedang mengetik" + ".".repeat(dotCount), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }
        }

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tanya Yogi Ario...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
        }

        // Templates (Pindah ke bawah v9.0)
        if (viewModel.messages.size < 3) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(templates) { template ->
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { viewModel.sendMessage(template) }
                    ) {
                        Text(
                            text = template,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
