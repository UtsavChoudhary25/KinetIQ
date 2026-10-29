package com.utsav.kinetiq.ui.workout.tracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utsav.kinetiq.domain.model.SetType
import com.utsav.kinetiq.domain.model.WorkoutExercise
import com.utsav.kinetiq.domain.model.WorkoutSet
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import com.utsav.kinetiq.ui.workout.WorkoutViewModel
import com.utsav.kinetiq.ui.workout.components.ExerciseAnimationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onFinishClick: () -> Unit,
    onCancelClick: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val workout by viewModel.activeWorkout.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isRestTimerActive by viewModel.isRestTimerActive.collectAsState()
    val restTimerSeconds by viewModel.restTimerSeconds.collectAsState()

    var showSummaryDialog by remember { mutableStateOf(false) }

    if (workout == null) return

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(workout?.title?.uppercase() ?: "WORKOUT", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        Text(formatTime(timerSeconds), style = MaterialTheme.typography.bodySmall.copy(color = KinetiqLime))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        showSummaryDialog = true
                    }) {
                        Text("FINISH", color = KinetiqLime, fontWeight = FontWeight.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = KinetiqBlack)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                workout?.exercises?.let { exercises ->
                    itemsIndexed(exercises) { exerciseIndex, exercise ->
                        ExerciseSection(
                            exercise = exercise,
                            onSetToggle = { setIndex ->
                                viewModel.toggleSetCompletion(exerciseIndex, setIndex)
                            },
                            onSetTypeChange = { setIndex, newType ->
                                viewModel.updateSetType(exerciseIndex, setIndex, newType)
                            }
                        )
                    }
                }
            }

            // Rest Timer Overlay
            AnimatedVisibility(
                visible = isRestTimerActive,
                enter = expandVertically(),
                exit = shrinkVertically(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                RestTimerComponent(
                    timeLeft = restTimerSeconds,
                    onSkip = { viewModel.skipRestTimer() },
                    onAdd30s = { viewModel.addRestTime(30) }
                )
            }

            if (showSummaryDialog) {
                val totalVolume = workout?.exercises?.sumOf { ex ->
                    ex.sets.filter { it.isCompleted }.sumOf { (it.weight * it.reps).toDouble() }
                } ?: 0.0

                val prCount = workout?.exercises?.sumOf { ex ->
                    ex.sets.count { it.isCompleted && it.isPersonalRecord }
                } ?: 0

                AlertDialog(
                    onDismissRequest = { showSummaryDialog = false },
                    containerColor = KinetiqCharcoal,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = KinetiqLime)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WORKOUT COMPLETE!", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Great session! Here is your workout summary:", color = KinetiqGray, style = MaterialTheme.typography.bodyMedium)

                            Row(
                                modifier = Modifier.fillMaxWidth().background(KinetiqBlack, RoundedCornerShape(12.dp)).padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("VOLUME", color = KinetiqGray, fontSize = 10.sp)
                                    Text("${totalVolume.toInt()} KG", color = KinetiqLime, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TIME", color = KinetiqGray, fontSize = 10.sp)
                                    Text(formatTime(timerSeconds), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("PRs", color = KinetiqGray, fontSize = 10.sp)
                                    Text("$prCount", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.finishWorkout()
                                showSummaryDialog = false
                                onFinishClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack)
                        ) {
                            Text("SAVE WORKOUT", fontWeight = FontWeight.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSummaryDialog = false }) {
                            Text("CONTINUE SESSION", color = KinetiqGray)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExerciseSection(
    exercise: WorkoutExercise,
    onSetToggle: (Int) -> Unit,
    onSetTypeChange: (Int, SetType) -> Unit
) {
    Column {
        ExerciseAnimationCard(
            exerciseName = exercise.exercise.name,
            muscleGroup = exercise.exercise.muscleGroup,
            equipment = exercise.exercise.equipment,
            instructions = exercise.exercise.instructions
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SETS & REPS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = KinetiqLime,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        exercise.sets.forEachIndexed { index, set ->
            SetRow(
                setNumber = index + 1,
                set = set,
                onToggle = { onSetToggle(index) },
                onSetTypeChange = { newType -> onSetTypeChange(index, newType) }
            )
        }
    }
}

@Composable
fun SetRow(
    setNumber: Int,
    set: WorkoutSet,
    onToggle: () -> Unit,
    onSetTypeChange: (SetType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = MaterialTheme.shapes.small,
                color = if (set.isCompleted) KinetiqLime else KinetiqCharcoal
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = setNumber.toString(),
                        color = if (set.isCompleted) KinetiqBlack else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${set.weight} KG x ${set.reps}", color = if (set.isCompleted) KinetiqGray else Color.White, fontWeight = FontWeight.Bold)

                    if (set.isPersonalRecord) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFF9800).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("🔥 PR!", color = Color(0xFFFF9800), fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                Text(
                    text = set.setType.name,
                    color = KinetiqGray,
                    fontSize = 10.sp
                )
            }
        }

        IconButton(
            onClick = onToggle,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (set.isCompleted) KinetiqLime else KinetiqCharcoal,
                contentColor = if (set.isCompleted) KinetiqBlack else KinetiqLime
            ),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun RestTimerComponent(
    timeLeft: Int,
    onSkip: () -> Unit,
    onAdd30s: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = KinetiqLime,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = KinetiqBlack)
                Spacer(modifier = Modifier.width(12.dp))
                Text("REST: ${timeLeft}S", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = KinetiqBlack))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onAdd30s) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = KinetiqBlack, modifier = Modifier.size(16.dp))
                        Text("30s", color = KinetiqBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                TextButton(onClick = onSkip) {
                    Text("SKIP", color = KinetiqBlack, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format("%d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
