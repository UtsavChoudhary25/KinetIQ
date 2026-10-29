package com.utsav.kinetiq.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.utsav.kinetiq.ui.theme.KinetiqTheme
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utsav.kinetiq.ui.home.components.HealthyRecipesCard
import com.utsav.kinetiq.ui.home.components.InsightCard
import com.utsav.kinetiq.ui.home.components.MetricCard
import com.utsav.kinetiq.ui.home.components.MotivationalQuoteCard
import com.utsav.kinetiq.ui.home.components.WorkoutCard
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onWorkoutStart: () -> Unit,
    onNavigateToRecipes: () -> Unit = {},
    onNavigateToMealScanner: () -> Unit = {},
    onNavigateToSpecializedPlans: () -> Unit = {},
    onNavigateToPromoShowcase: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val healthConnectManager = viewModel.getHealthConnectManager

    val permissionContract = remember(healthConnectManager) {
        PermissionController.createRequestPermissionResultContract()
    }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = permissionContract
    ) {
        viewModel.syncGoogleFit()
    }

    val currentDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    val greeting = remember { getGreeting() }

    Scaffold(
        containerColor = KinetiqBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            if (!uiState.isGoogleFitConnected) {
                GoogleFitPromptBanner(
                    onConnectClick = {
                        scope.launch {
                            if (healthConnectManager.isHealthConnectAvailable()) {
                                if (healthConnectManager.hasAllPermissions()) {
                                    viewModel.syncGoogleFit()
                                } else {
                                    requestPermissionsLauncher.launch(healthConnectManager.permissions)
                                }
                            } else {
                                viewModel.syncGoogleFit()
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = KinetiqGray,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = uiState.userName.uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                }
                
                // Streak Indicator
                Surface(
                    color = KinetiqLime.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥 ${uiState.workoutStreak} DAY STREAK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentDate.uppercase(), style = MaterialTheme.typography.labelSmall, color = KinetiqGray)

            Spacer(modifier = Modifier.height(28.dp))

            // Progress Header Row with Google Fit Sync Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S PROGRESS",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                )

                if (uiState.isGoogleFitConnected) {
                    TextButton(
                        onClick = { viewModel.syncGoogleFit() },
                        colors = ButtonDefaults.textButtonColors(contentColor = KinetiqLime)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Google Fit Synced",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Grid (Row 1: Calories & Steps)
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Calories",
                    value = uiState.dailyCalories.toString(),
                    target = uiState.targetCalories.toString(),
                    progress = uiState.dailyCalories.toFloat() / uiState.targetCalories,
                    unit = "kcal",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                MetricCard(
                    title = "Steps",
                    value = String.format("%,d", uiState.dailySteps),
                    target = String.format("%,d", uiState.targetSteps),
                    progress = uiState.dailySteps.toFloat() / uiState.targetSteps,
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Grid (Row 2: Distance KM & Water)
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Distance",
                    value = String.format("%.2f", uiState.dailyDistanceKm),
                    target = String.format("%.1f", uiState.targetDistanceKm),
                    progress = uiState.dailyDistanceKm / uiState.targetDistanceKm,
                    unit = "KM",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                MetricCard(
                    title = "Water Intake",
                    value = String.format("%.1f", uiState.dailyWater),
                    target = String.format("%.1f", uiState.targetWater),
                    progress = uiState.dailyWater / uiState.targetWater,
                    unit = "L",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Water Add Buttons
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HYDRATION TRACKER", color = KinetiqLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${uiState.waterLoggedMl} ml / ${uiState.targetWaterMl} ml", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.addWater(250) },
                            colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime.copy(alpha = 0.2f), contentColor = KinetiqLime),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+250ml", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.addWater(500) },
                            colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+500ml", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Readiness / Recovery Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡ RECOVERY READINESS", color = KinetiqGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(
                            color = KinetiqLime,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("${uiState.readinessScore}% - ${uiState.readinessLabel.uppercase()}", color = KinetiqBlack, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { uiState.readinessScore / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = KinetiqLime,
                        trackColor = KinetiqBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        uiState.readinessRecommendation,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MealScannerCard(
                onScanClick = onNavigateToMealScanner
            )

            Spacer(modifier = Modifier.height(16.dp))

            PromoShowcaseBannerCard(
                onWatchClick = onNavigateToPromoShowcase
            )

            Spacer(modifier = Modifier.height(32.dp))

            WorkoutCard(
                title = uiState.todayWorkoutTitle,
                duration = uiState.todayWorkoutDuration,
                onStartClick = onWorkoutStart
            )

            Spacer(modifier = Modifier.height(24.dp))

            SpecializedPlansCard(onClick = onNavigateToSpecializedPlans)

            Spacer(modifier = Modifier.height(24.dp))

            HealthyRecipesCard(
                onExploreClick = onNavigateToRecipes
            )

            Spacer(modifier = Modifier.height(24.dp))

            InsightCard(insight = uiState.aiInsight)
            
            Spacer(modifier = Modifier.height(24.dp))

            MotivationalQuoteCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GoogleFitPromptBanner(
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(KinetiqLime.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = KinetiqLime.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = KinetiqLime,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "CONNECT GOOGLE FIT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqLime,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Text(
                        text = "Auto-sync steps, calories & distance",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Button(
                onClick = onConnectClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetiqLime,
                    contentColor = KinetiqBlack
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("CONNECT", fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun MealScannerCard(
    onScanClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.15f)
            ),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = KinetiqLime.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = KinetiqLime,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "AI MEAL SCANNER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqLime,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Scan Food for Calories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetiqLime,
                    contentColor = KinetiqBlack
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("SCAN AI", fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun SpecializedPlansCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
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
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = KinetiqLime.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("⚡", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "SPECIALIZED DISCIPLINE PLANS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqLime,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Athlete, Bodybuilding & Shred",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetiqLime,
                    contentColor = KinetiqBlack
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text("EXPLORE", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PromoShowcaseBannerCard(
    onWatchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.25f)
            ),
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.5f),
                    Color(0xFFA855F7).copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = KinetiqLime.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎬", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "1-MIN PROMO SHOWCASE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqLime,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Interactive Feature Trailer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KinetiqLime,
                    contentColor = KinetiqBlack
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text("WATCH 🚀", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 4..11 -> "GOOD MORNING,"
        in 12..16 -> "GOOD AFTERNOON,"
        else -> "GOOD EVENING,"
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    KinetiqTheme {
        HomeScreen(
            onWorkoutStart = {},
            onNavigateToRecipes = {},
            onNavigateToMealScanner = {},
            onNavigateToSpecializedPlans = {},
            onNavigateToPromoShowcase = {}
        )
    }
}
