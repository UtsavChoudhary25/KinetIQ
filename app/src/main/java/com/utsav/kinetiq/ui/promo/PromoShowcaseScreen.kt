package com.utsav.kinetiq.ui.promo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.ui.theme.*
import kotlinx.coroutines.delay

data class PromoScene(
    val title: String,
    val subtitle: String,
    val voiceoverSubtitle: String,
    val tag: String,
    val icon: ImageVector
)

val promoScenes = listOf(
    PromoScene(
        title = "FITNESS JUST EVOLVED",
        subtitle = "Meet Kinetiq – Your complete AI health & fitness ecosystem.",
        voiceoverSubtitle = "\"Stop guessing your workouts and manually counting calories. Meet Kinetiq – your all-in-one AI fitness engine.\"",
        tag = "01 // NEXT-GEN ECOSYSTEM",
        icon = Icons.Default.AutoAwesome
    ),
    PromoScene(
        title = "REAL-TIME VISION AI",
        subtitle = "On-device computer vision counts reps & perfects form.",
        voiceoverSubtitle = "\"Turn your phone camera into a personal coach. Real-time pose tracking counts reps and guides form with instant audio cues.\"",
        tag = "02 // POSE & REP DETECTOR",
        icon = Icons.Default.Visibility
    ),
    PromoScene(
        title = "GEMINI MEAL SCANNER",
        subtitle = "Point your camera at any dish for instant calorie & macro breakdown.",
        voiceoverSubtitle = "\"Fueling your body made simple. Snap a photo of any meal, and Gemini AI calculates calories and macros in seconds.\"",
        tag = "03 // AI NUTRITION SCANNER",
        icon = Icons.Default.Restaurant
    ),
    PromoScene(
        title = "24/7 AI COACH & PLANS",
        subtitle = "Personalized routine generation & intelligent fitness advisor.",
        voiceoverSubtitle = "\"Stay ahead with daily recovery scores, smart workout generators, and a 24/7 AI coach ready for any question.\"",
        tag = "04 // AI WORKOUT & COACH",
        icon = Icons.Default.Psychology
    ),
    PromoScene(
        title = "UNLEASH PEAK PERFORMANCE",
        subtitle = "Take total control of your fitness journey today.",
        voiceoverSubtitle = "\"No hassle. Just real results. Download Kinetiq today and unleash your peak potential!\"",
        tag = "05 // KINETIQ HIGH-PERFORMANCE",
        icon = Icons.Default.FitnessCenter
    )
)

@Composable
fun PromoShowcaseScreen(
    onCloseClick: () -> Unit,
    onNavigateToVision: () -> Unit = {},
    onNavigateToMealScanner: () -> Unit = {},
    onNavigateToCoach: () -> Unit = {},
    onNavigateToWorkouts: () -> Unit = {}
) {
    var currentSceneIndex by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isMuted by remember { mutableStateOf(false) }

    val currentScene = promoScenes[currentSceneIndex]

    // 12 Seconds per slide timer (Total 60 seconds for 5 slides)
    LaunchedEffect(currentSceneIndex, isPaused) {
        if (!isPaused) {
            val stepMs = 50L
            val totalMs = 12000f
            while (progress < 1f) {
                delay(stepMs)
                if (!isPaused) {
                    progress += (stepMs / totalMs)
                }
            }
            progress = 0f
            if (currentSceneIndex < promoScenes.size - 1) {
                currentSceneIndex++
            } else {
                currentSceneIndex = 0 // Loop back
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KinetiqBlack)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        if (offset.x < size.width / 3) {
                            if (currentSceneIndex > 0) {
                                currentSceneIndex--
                                progress = 0f
                            }
                        } else if (offset.x > size.width * 2 / 3) {
                            if (currentSceneIndex < promoScenes.size - 1) {
                                currentSceneIndex++
                                progress = 0f
                            } else {
                                currentSceneIndex = 0
                                progress = 0f
                            }
                        }
                    }
                )
            }
    ) {
        // Glowing Ambient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Story Progress Bars & Controls
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Multi-segment progress bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    promoScenes.forEachIndexed { index, _ ->
                        val barProgress = when {
                            index < currentSceneIndex -> 1f
                            index == currentSceneIndex -> progress
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { barProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = KinetiqLime,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Top Bar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = KinetiqLime.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "KINETIQ 1-MIN PROMO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp)
                            )
                            Text(
                                if (isPaused) "PAUSED (HOLDING)" else "TAP RIGHT FOR NEXT",
                                style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontSize = 9.sp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier.size(36.dp).background(KinetiqCharcoal, CircleShape)
                        ) {
                            Icon(
                                if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Toggle Mute",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onCloseClick,
                            modifier = Modifier.size(36.dp).background(KinetiqCharcoal, CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Central Dynamic Scene Animation View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentSceneIndex,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    },
                    label = "promo_scene_transition"
                ) { targetIndex ->
                    when (targetIndex) {
                        0 -> SceneDashboardPreview()
                        1 -> SceneVisionAiPreview()
                        2 -> SceneMealScannerPreview()
                        3 -> SceneCoachPreview()
                        else -> SceneOutroEcosystemPreview()
                    }
                }
            }

            // Bottom Voiceover Subtitle & Action Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Voiceover Caption Box
                Surface(
                    color = KinetiqCharcoal.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                currentScene.tag,
                                style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentScene.voiceoverSubtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Launch Feature Action Button
                val (buttonText, action) = when (currentSceneIndex) {
                    1 -> "TRY VISION AI CAMERA" to onNavigateToVision
                    2 -> "LAUNCH MEAL SCANNER" to onNavigateToMealScanner
                    3 -> "TALK TO AI COACH" to onNavigateToCoach
                    4 -> "EXPLORE ALL WORKOUTS" to onNavigateToWorkouts
                    else -> "TRY KINETIQ HUB" to onCloseClick
                }

                Button(
                    onClick = action,
                    colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = KinetiqLime)
                ) {
                    Icon(currentScene.icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(buttonText, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

// ==================== SCENE PREVIEW COMPONENTS ====================

@Composable
fun SceneDashboardPreview() {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(KinetiqLime, KinetiqPurple)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = KinetiqLime.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "🔥 7 DAY STREAK",
                    color = KinetiqLime,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp
                )
            }

            // Readiness Radial Gauge Simulation
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                CircularProgressIndicator(
                    progress = { 0.92f },
                    modifier = Modifier.fillMaxSize(),
                    color = KinetiqLime,
                    strokeWidth = 14.dp,
                    trackColor = KinetiqBlack
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("92%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp)
                    Text("READINESS", color = KinetiqGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniMetricBadge("CALORIES", "1,850", "2,200", KinetiqLime)
                MiniMetricBadge("STEPS", "8,420", "10,000", Color(0xFF00E5FF))
                MiniMetricBadge("WATER", "2.5L", "3.0L", Color(0xFFA855F7))
            }
        }
    }
}

@Composable
fun SceneVisionAiPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "rep_anim")
    val repCountFloat by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "reps"
    )
    val repCount = repCountFloat.toInt()

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = KinetiqBlack),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, KinetiqLime)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Simulated Camera Grid & Skeleton Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2

                // Pose skeleton lines simulation
                drawLine(KinetiqLime, Offset(cx, cy - 100), Offset(cx, cy + 40), strokeWidth = 8f)
                drawLine(KinetiqLime, Offset(cx, cy - 60), Offset(cx - 80, cy), strokeWidth = 8f)
                drawLine(KinetiqLime, Offset(cx, cy - 60), Offset(cx + 80, cy), strokeWidth = 8f)
                drawLine(KinetiqLime, Offset(cx, cy + 40), Offset(cx - 60, cy + 180), strokeWidth = 8f)
                drawLine(KinetiqLime, Offset(cx, cy + 40), Offset(cx + 60, cy + 180), strokeWidth = 8f)

                // Joint circles
                drawCircle(Color.White, 12f, Offset(cx, cy - 100))
                drawCircle(KinetiqLime, 10f, Offset(cx - 80, cy))
                drawCircle(KinetiqLime, 10f, Offset(cx + 80, cy))
                drawCircle(KinetiqLime, 10f, Offset(cx - 60, cy + 180))
                drawCircle(KinetiqLime, 10f, Offset(cx + 60, cy + 180))
            }

            // Overlay Badges
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = KinetiqBlack.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KinetiqLime)
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CenterFocusStrong, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("VISION AI: SQUAT FORM 90°", color = KinetiqLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("SQUAT REPS", color = KinetiqGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$repCount", color = KinetiqLime, fontSize = 54.sp, fontWeight = FontWeight.Black)
                    }

                    Surface(
                        color = KinetiqLime,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("GREAT FORM! 🔊", color = KinetiqBlack, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SceneMealScannerPreview() {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = KinetiqLime.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                    Text("GEMINI 1.5 FLASH SCAN", color = KinetiqLime, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                Text("650 KCAL", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(KinetiqBlack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(64.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroPill("PROTEIN", "48g", KinetiqLime)
                MacroPill("CARBS", "52g", Color(0xFFFFB74D))
                MacroPill("FATS", "18g", Color(0xFF64B5F6))
            }
        }
    }
}

@Composable
fun SceneCoachPreview() {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, KinetiqPurple)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = KinetiqPurple, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("24/7 AI COACH", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Surface(
                color = KinetiqBlack,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, KinetiqGray.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("USER:", color = KinetiqGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("\"Generate a 45-min chest workout with dumbbell focus\"", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("AI COACH:", color = KinetiqLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("\"Here is your optimized Chest & Triceps routine with 4 sets of Incline Dumbbell Press!\"", color = KinetiqLime, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Surface(
                color = KinetiqPurple.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, KinetiqPurple)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚡ SPECIALIZED PROGRAM: SHRED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = KinetiqPurple)
                }
            }
        }
    }
}

@Composable
fun SceneOutroEcosystemPreview() {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(KinetiqLime, KinetiqPurple)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = KinetiqLime,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = KinetiqBlack, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("KINETIQ", style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 2.sp))
            Text("THE FUTURE OF FITNESS IS HERE", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureChip("VISION AI", Modifier.weight(1f))
                FeatureChip("MEAL SCAN", Modifier.weight(1f))
                FeatureChip("AI COACH", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MiniMetricBadge(label: String, valStr: String, targetStr: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valStr, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text("/ $targetStr", color = KinetiqGray, fontSize = 9.sp)
        Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MacroPill(label: String, valStr: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valStr, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FeatureChip(title: String, modifier: Modifier = Modifier) {
    Surface(
        color = KinetiqBlack,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(title, color = KinetiqLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromoShowcaseScreenPreview() {
    KinetiqTheme {
        PromoShowcaseScreen(
            onCloseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SceneVisionAiPreviewPreview() {
    KinetiqTheme {
        Box(modifier = Modifier.fillMaxSize().background(KinetiqBlack).padding(20.dp)) {
            SceneVisionAiPreview()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SceneMealScannerPreviewPreview() {
    KinetiqTheme {
        Box(modifier = Modifier.fillMaxSize().background(KinetiqBlack).padding(20.dp)) {
            SceneMealScannerPreview()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SceneCoachPreviewPreview() {
    KinetiqTheme {
        Box(modifier = Modifier.fillMaxSize().background(KinetiqBlack).padding(20.dp)) {
            SceneCoachPreview()
        }
    }
}
