package com.utsav.kinetiq.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICoachScreen(
    viewModel: AICoachViewModel
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading
    val isApiKeyMissing by viewModel.isApiKeyMissing
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = KinetiqLime)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI COACH", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp))
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "AI Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = KinetiqBlack, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (showSettingsDialog) {
            ApiKeyDialog(
                onDismiss = { showSettingsDialog = false },
                onSave = { 
                    viewModel.updateApiKey(it)
                    showSettingsDialog = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isApiKeyMissing) {
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth().clickable { showSettingsDialog = true }
                ) {
                    Text(
                        "Gemini API Key is missing. Tap to setup.",
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            CircularProgressIndicator(color = KinetiqLime, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    }
                }
            }

            // Input Area
            Surface(
                color = KinetiqCharcoal,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask anything...", color = KinetiqGray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    IconButton(
                        onClick = {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                        enabled = inputText.isNotBlank() && !isLoading
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ApiKeyDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var key by remember { mutableStateOf("") }
    var keyVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = KinetiqLime)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI API Key Settings", color = Color.White) 
            }
        },
        text = {
            Column {
                Text("API key is auto-applied upon installation. You can view or update it below:", color = KinetiqGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("API Key") },
                    placeholder = { Text("sk-or-... or AIza...", color = KinetiqGray) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (keyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { keyVisible = !keyVisible }) {
                            Icon(image, contentDescription = "Toggle Key Visibility", tint = KinetiqLime)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = KinetiqCharcoal,
                        unfocusedContainerColor = KinetiqCharcoal,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = KinetiqLime,
                        unfocusedIndicatorColor = KinetiqGray
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("🔒 Encrypted & Hidden for Security", color = KinetiqLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(key) },
                colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack)
            ) {
                Text("SAVE & APPLY", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = KinetiqGray)
            }
        },
        containerColor = KinetiqCharcoal
    )
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isUser) KinetiqLime else KinetiqCharcoal
    val textColor = if (message.isUser) KinetiqBlack else Color.White
    val shape = if (message.isUser) {
        RoundedCornerShape(24.dp, 24.dp, 4.dp, 24.dp)
    } else {
        RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = color,
            shape = shape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(color = textColor, lineHeight = 20.sp)
            )
        }
    }
}
