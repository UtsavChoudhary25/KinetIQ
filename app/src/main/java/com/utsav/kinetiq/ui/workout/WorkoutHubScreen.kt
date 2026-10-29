package com.utsav.kinetiq.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utsav.kinetiq.domain.model.Workout
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@Composable
fun WorkoutHubScreen(
    onNavigateToGenerator: () -> Unit,
    onNavigateToTracker: (Workout) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSpecializedPlans: () -> Unit = {},
    viewModel: WorkoutViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()

    Scaffold(
        containerColor = KinetiqBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToGenerator,
                containerColor = KinetiqLime,
                contentColor = KinetiqBlack,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = KinetiqLime.copy(alpha = 0.5f)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Workout")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                Text(
                    text = "WORKOUT HUB",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                )
            }

            // AI Generator Card
            item {
                AICard(onClick = onNavigateToGenerator)
            }

            // Specialized Disciplines Banner
            item {
                SpecializedPlansBanner(onClick = onNavigateToSpecializedPlans)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MY WORKOUT ROUTINES",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = KinetiqGray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = KinetiqGray)
                    }
                }
            }

            items(workouts) { workout ->
                WorkoutItem(
                    workout = workout,
                    onClick = { onNavigateToTracker(workout) }
                )
            }
        }
    }
}

@Composable
fun AICard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = KinetiqLime.copy(alpha = 0.4f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        color = KinetiqLime
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            KinetiqLime,
                            Color(0xFFB0EC00)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = KinetiqBlack.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = KinetiqBlack,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = "GENERATE WITH AI",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = KinetiqBlack,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Custom hyper-personalized plan by KINETIQ AI",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = KinetiqBlack.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.12f)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    KinetiqLime.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            KinetiqLime.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(22.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = workout.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${workout.exercises.size} EXERCISES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqGray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = " • ",
                        color = KinetiqGray
                    )
                    Text(
                        text = "${workout.durationMinutes} MINS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqGray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            
            Surface(
                color = KinetiqLime.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.25f))
            ) {
                Text(
                    text = workout.difficulty.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = KinetiqLime,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SpecializedPlansBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.2f)
            )
            .clickable { onClick() },
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
                        Text("🏋️‍♂️", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "SPECIALIZED PRO PLANS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqLime,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Athlete, Bodybuilding, Powerlifting",
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

