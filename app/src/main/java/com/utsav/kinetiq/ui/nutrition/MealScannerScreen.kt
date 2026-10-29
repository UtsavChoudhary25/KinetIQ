package com.utsav.kinetiq.ui.nutrition

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.domain.model.MealAnalysisResult
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScannerScreen(
    onBackClick: () -> Unit,
    onLogCalories: (Int) -> Unit,
    viewModel: MealScannerViewModel
) {
    val context = LocalContext.current
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.onImageSelected(bitmap)
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            if (bitmap != null) {
                viewModel.onImageSelected(bitmap)
            }
        }
    }

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = KinetiqLime)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI MEAL SCANNER",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KinetiqBlack,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(KinetiqCharcoal)
                    .border(1.dp, KinetiqGray.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Meal",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = KinetiqLime,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "SNAP OR SELECT A MEAL PHOTO",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = KinetiqGray,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Selection Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqLime),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(KinetiqLime))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CAMERA", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(KinetiqGray))
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GALLERY", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            var showVoiceDialog by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { showVoiceDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KinetiqLime),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(KinetiqLime.copy(alpha = 0.5f)))
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOG VIA VOICE / SPEECH TEXT", fontWeight = FontWeight.Bold)
            }

            if (showVoiceDialog) {
                var voiceText by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showVoiceDialog = false },
                    containerColor = KinetiqCharcoal,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = KinetiqLime)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Describe Your Meal", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "Type or speak what you ate (e.g. '2 scrambled eggs and 2 slices of whole wheat toast'):",
                                style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                            )
                            OutlinedTextField(
                                value = voiceText,
                                onValueChange = { voiceText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. 1 bowl chicken curry with 2 rotis", color = KinetiqGray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = KinetiqBlack,
                                    unfocusedContainerColor = KinetiqBlack,
                                    focusedBorderColor = KinetiqLime,
                                    unfocusedBorderColor = KinetiqGray.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.analyzeSpokenMeal(voiceText)
                                showVoiceDialog = false
                            },
                            enabled = voiceText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack)
                        ) {
                            Text("Analyze Voice Meal", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showVoiceDialog = false }) {
                            Text("Cancel", color = KinetiqGray)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Analyze Action Button
            if (selectedBitmap != null && uiState !is MealScannerUiState.Analyzing) {
                Button(
                    onClick = { viewModel.analyzeMeal() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KinetiqLime,
                        contentColor = KinetiqBlack
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ANALYZE MEAL WITH AI", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // State Content
            when (val state = uiState) {
                is MealScannerUiState.Analyzing -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = KinetiqLime, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "KINETIQ AI IS ANALYZING YOUR MEAL...",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Calculating calories, macros, and ingredients",
                                style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                            )
                        }
                    }
                }

                is MealScannerUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Analysis Failed",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                        }
                    }
                }

                is MealScannerUiState.Success -> {
                    MealAnalysisResultView(
                        result = state.result,
                        onLogToDashboard = {
                            onLogCalories(state.result.estimatedCalories)
                            onBackClick()
                        }
                    )
                }

                MealScannerUiState.Idle -> {
                    // Show prompt if no image yet
                    if (selectedBitmap == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = KinetiqLime,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Powered by Gemini 1.5 Flash",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Take a photo of any dish to instantly estimate calories, protein, carbs, and fats.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealAnalysisResultView(
    result: MealAnalysisResult,
    onLogToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Food Title & Calorie Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = KinetiqLime.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = result.healthRating.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = KinetiqLime,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${result.estimatedCalories} KCAL",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = result.foodName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        // Macronutrients Grid Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "MACRONUTRIENTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = KinetiqGray,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MacroNutrientItem(
                        label = "Protein",
                        value = "${result.proteinGrams.toInt()}g",
                        color = KinetiqLime
                    )
                    MacroNutrientItem(
                        label = "Carbs",
                        value = "${result.carbsGrams.toInt()}g",
                        color = Color(0xFFFFB74D) // Amber/Orange
                    )
                    MacroNutrientItem(
                        label = "Fats",
                        value = "${result.fatsGrams.toInt()}g",
                        color = Color(0xFF64B5F6) // Light Blue
                    )
                }
            }
        }

        // Breakdown Items Card
        if (result.breakdownItems.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "DETECTED INGREDIENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqGray,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    result.breakdownItems.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(KinetiqLime)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                            )
                        }
                    }
                }
            }
        }

        // AI Tip Card
        if (result.tips.isNotBlank()) {
            Surface(
                color = KinetiqLime.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = KinetiqLime,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = result.tips,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White, lineHeight = 20.sp)
                    )
                }
            }
        }

        // Action: Log Calories Button
        Button(
            onClick = onLogToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KinetiqLime,
                contentColor = KinetiqBlack
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LOG ${result.estimatedCalories} KCAL TO TODAY'S GOAL",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MacroNutrientItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = KinetiqGray,
                letterSpacing = 1.sp
            )
        )
    }
}
