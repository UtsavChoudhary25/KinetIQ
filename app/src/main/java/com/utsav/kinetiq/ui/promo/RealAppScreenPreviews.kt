package com.utsav.kinetiq.ui.promo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.domain.model.MealAnalysisResult
import com.utsav.kinetiq.ui.home.MealScannerCard
import com.utsav.kinetiq.ui.home.SpecializedPlansCard
import com.utsav.kinetiq.ui.home.components.MetricCard
import com.utsav.kinetiq.ui.home.components.MotivationalQuoteCard
import com.utsav.kinetiq.ui.nutrition.MealAnalysisResultView
import com.utsav.kinetiq.ui.theme.*

// ==================== REAL HOME DASHBOARD PREVIEW ====================
@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
fun RealHomeScreenPreview() {
    KinetiqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = KinetiqBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GOOD MORNING,",
                            style = MaterialTheme.typography.labelMedium.copy(color = KinetiqGray, letterSpacing = 1.sp)
                        )
                        Text(
                            text = "ATHLETE",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = Color.White)
                        )
                    }

                    Surface(
                        color = KinetiqLime.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "🔥 7 DAY STREAK",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Readiness Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡ RECOVERY READINESS", color = KinetiqGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Surface(color = KinetiqLime, shape = RoundedCornerShape(8.dp)) {
                                Text("92% - OPTIMAL", color = KinetiqBlack, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { 0.92f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = KinetiqLime,
                            trackColor = KinetiqBlack
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Prime recovery state! High CNS readiness for heavy squats or intensive HIIT.",
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metrics Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "Calories",
                        value = "1,850",
                        target = "2,200",
                        progress = 0.84f,
                        unit = "kcal",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MetricCard(
                        title = "Steps",
                        value = "8,420",
                        target = "10,000",
                        progress = 0.84f,
                        unit = "",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MealScannerCard(onScanClick = {})

                Spacer(modifier = Modifier.height(16.dp))

                SpecializedPlansCard(onClick = {})
            }
        }
    }
}

// ==================== REAL VISION AI CAMERA PREVIEW ====================
@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
fun RealVisionAiScreenPreview() {
    KinetiqTheme {
        Box(modifier = Modifier.fillMaxSize().background(KinetiqBlack)) {
            // Skeleton Canvas Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2

                drawLine(KinetiqLime, Offset(cx, cy - 120), Offset(cx, cy + 40), strokeWidth = 10f)
                drawLine(KinetiqLime, Offset(cx, cy - 80), Offset(cx - 100, cy), strokeWidth = 10f)
                drawLine(KinetiqLime, Offset(cx, cy - 80), Offset(cx + 100, cy), strokeWidth = 10f)
                drawLine(KinetiqLime, Offset(cx, cy + 40), Offset(cx - 80, cy + 200), strokeWidth = 10f)
                drawLine(KinetiqLime, Offset(cx, cy + 40), Offset(cx + 80, cy + 200), strokeWidth = 10f)

                for (pt in listOf(Offset(cx, cy - 120), Offset(cx - 100, cy), Offset(cx + 100, cy), Offset(cx - 80, cy + 200), Offset(cx + 80, cy + 200))) {
                    drawCircle(Color.White, 12f, pt)
                    drawCircle(KinetiqLime, 8f, pt)
                }
            }

            // Controls Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = KinetiqBlack.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VISION AI ACTIVE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = KinetiqLime, letterSpacing = 1.sp))
                        }
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(44.dp)
                            .background(KinetiqBlack.copy(alpha = 0.8f), CircleShape)
                            .border(1.dp, KinetiqLime.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Exercise selector pill
                Surface(
                    color = KinetiqLime,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🏋️ SQUAT", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = KinetiqBlack))
                }
            }

            // Feedback Card & Angle
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 120.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = KinetiqLime,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "PERFECT FORM! SQUAT DEPTH REACHED",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = KinetiqBlack)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Joint Angle: 90°",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Rep Counter
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text("SQUAT REPS", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp))
                    Text(
                        text = "12",
                        color = KinetiqLime,
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}

// ==================== REAL MEAL SCANNER PREVIEW ====================
@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
fun RealMealScannerPreview() {
    KinetiqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = KinetiqBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = KinetiqLime)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI MEAL SCANNER", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = Color.White))
                }

                Spacer(modifier = Modifier.height(16.dp))

                MealAnalysisResultView(
                    result = MealAnalysisResult(
                        foodName = "Grilled Chicken Quinoa Bowl",
                        estimatedCalories = 650,
                        proteinGrams = 48f,
                        carbsGrams = 52f,
                        fatsGrams = 18f,
                        healthRating = "Optimal Fuel",
                        breakdownItems = listOf("Grilled Chicken Breast (200g)", "Quinoa & Salad (150g)", "Fresh Avocado & Olive Oil"),
                        tips = "High protein meal ideal for post-workout muscle recovery and sustained energy."
                    ),
                    onLogToDashboard = {}
                )
            }
        }
    }
}

// ==================== REAL AI COACH SCREEN PREVIEW ====================
@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
fun RealAICoachPreview() {
    KinetiqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = KinetiqBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = KinetiqLime)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI COACH", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = Color.White))
                    }
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // User Bubble
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Surface(
                        color = KinetiqLime,
                        shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = "Generate a 45-min chest workout focused on dumbbell strength",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(color = KinetiqBlack, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Coach Bubble
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Surface(
                        color = KinetiqCharcoal,
                        shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                        modifier = Modifier.widthIn(max = 290.dp),
                        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "I've generated a high-intensity Chest & Triceps routine featuring Incline Dumbbell Press, Dips, and Cable Flyes!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, lineHeight = 20.sp)
                        )
                    }
                }
            }
        }
    }
}
