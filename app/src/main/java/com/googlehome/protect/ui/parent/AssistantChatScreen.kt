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
            messages.add("assistant" to response)
            isLoading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantChatScreen(viewModel: AssistantViewModel = viewModel()) {
    var inputText by remember { mutableStateOf("") }
    
    val templates = listOf(
        "Bagaimana cara kerja Mode Kalkulator?",
        "Cara mengatur batas aman Geofence?",
        "Mengapa ETA tidak muncul?",
        "Bagaimana Cara Install Mode Anak beserta cara settingnya?"
    )

    Column(modifier = Modifier.fillMaxSize()) {
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
                            .background(if (isUser) Color(0xFF005AC1) else Color(0xFFE8F0FE))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.second,
                            color = if (isUser) Color.White else Color.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Templates
        if (viewModel.messages.size < 3) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(templates) { template ->
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEFF4FC),
                        onClick = { viewModel.sendMessage(template) }
                    ) {
                        Text(
                            text = template,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            color = Color(0xFF005AC1)
                        )
                    }
                }
            }
        }

        if (viewModel.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Yogi Ario sedang mengetik...", fontSize = 12.sp, color = Color.Gray)
            }
        }

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tanya Yogi Ario...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF005AC1)
                )
            )
            Spacer(Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                },
                containerColor = Color(0xFF005AC1),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
            }
        }
    }
}
