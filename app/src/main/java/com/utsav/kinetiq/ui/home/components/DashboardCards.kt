package com.utsav.kinetiq.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@Composable
fun MetricCard(
    title: String,
    value: String,
    target: String,
    progress: Float,
    unit: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "metric_progress"
    )

    Card(
        modifier = modifier
            .height(185.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.15f)
            ),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = KinetiqGray,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    val strokeWidthPx = 8.dp.toPx()
                    val diameter = size.width - strokeWidthPx
                    val topLeft = Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)
                    val arcSize = Size(diameter, diameter)

                    // Track Background Ring
                    drawArc(
                        color = KinetiqGray.copy(alpha = 0.12f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )

                    // Progress Ring with Gradient Sweep
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                KinetiqLime.copy(alpha = 0.7f),
                                KinetiqLime,
                                Color(0xFF00FF9D)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )
                }
            }

            Text(
                text = "/ $target $unit",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = KinetiqGray,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun WorkoutCard(
    title: String,
    duration: Int,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.4f),
                    Color.Transparent
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TODAY'S WORKOUT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = KinetiqLime,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚡ $duration MINS INTERMEDIATE",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KinetiqGray,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetiqLime,
                    contentColor = KinetiqBlack
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 14.dp)
            ) {
                Text("START", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun HealthyRecipesCard(
    onExploreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.15f)
            ),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.1f),
                    KinetiqLime.copy(alpha = 0.25f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            KinetiqLime.copy(alpha = 0.06f)
                        )
                    )
                )
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FITNESS NUTRITION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = KinetiqLime,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Healthy Recipes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🥗 30 High-Protein Meals",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KinetiqGray,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Button(
                onClick = onExploreClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetiqLime,
                    contentColor = KinetiqBlack
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 14.dp)
            ) {
                Text("EXPLORE", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun InsightCard(insight: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = KinetiqCharcoal,
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.3f),
                    Color.Transparent
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = KinetiqLime.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💡 AI INSIGHT",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "\"$insight\"",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

