package com.utsav.kinetiq.ui.workout.generator

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import com.utsav.kinetiq.ui.workout.WorkoutViewModel
import com.utsav.kinetiq.ui.workout.components.ExerciseAnimationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutGeneratorScreen(
    onBackClick: () -> Unit,
    onWorkoutGenerated: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val context = LocalContext.current

    var selectedGoal by remember { mutableStateOf("Hypertrophy") }
    var selectedMuscleGroup by remember { mutableStateOf("Full Body") }
    var selectedTime by remember { mutableStateOf(45) }
    var selectedEquipment by remember { mutableStateOf("Full Gym") }
    var selectedFitnessLevel by remember { mutableStateOf("Intermediate") }
    var selectedWorkoutStyle by remember { mutableStateOf("Straight Sets") }
    var selectedLimitations by remember { mutableStateOf("None") }

    val scrollState = rememberScrollState()
    val isGenerating by viewModel.isGenerating
    val generatedWorkout by viewModel.generatedWorkout.collectAsState()

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (generatedWorkout != null) "GENERATED PLAN" else "AI GENERATOR", 
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 2.sp
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (generatedWorkout != null) {
                            viewModel.clearGeneratedWorkout()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KinetiqBlack,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        val currentWorkout = generatedWorkout

        if (currentWorkout != null) {
            // Generated Workout Preview Screen with Exercise Animations
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Header Banner
                Surface(
                    color = KinetiqCharcoal,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = KinetiqLime,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI GENERATED SESSION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = KinetiqLime,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                )
                            }

                            Surface(
                                color = KinetiqLime.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = currentWorkout.difficulty.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = KinetiqLime,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentWorkout.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${currentWorkout.durationMinutes} MINS • ${currentWorkout.exercises.size} EXERCISES",
                            style = MaterialTheme.typography.bodyMedium.copy(color = KinetiqGray)
                        )
                    }
                }

                if (currentWorkout.aiRationale.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = KinetiqLime.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = KinetiqLime,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AI SCIENCE RATIONALE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = KinetiqLime,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentWorkout.aiRationale,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "EXERCISE ANIMATIONS & FORM GUIDES",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // List of Generated Exercises with Animated Motions
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    currentWorkout.exercises.forEach { workoutExercise ->
                        val ex = workoutExercise.exercise
                        ExerciseAnimationCard(
                            exerciseName = ex.name,
                            muscleGroup = ex.muscleGroup,
                            equipment = ex.equipment,
                            instructions = ex.instructions.ifBlank { "Perform ${workoutExercise.sets.size} sets with target control and proper breathing rhythm." }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.startGeneratedWorkout()
                        onWorkoutGenerated()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = KinetiqBlack
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("START WORKOUT SESSION", fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.saveGeneratedWorkoutToLibrary()
                        Toast.makeText(context, "Workout saved to My Workouts!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.5f)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = KinetiqLime,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SAVE TO MY WORKOUTS", fontWeight = FontWeight.Bold, color = KinetiqLime)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        viewModel.clearGeneratedWorkout()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = KinetiqGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RE-GENERATE WITH NEW PREFERENCES", color = KinetiqGray)
                    }
                }
            }
        } else {
            // Generator Preferences Configuration Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                Text(
                    text = "BUILD YOUR SESSION",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = Color.White)
                )
                Text(text = "Specify detailed preferences for Gemini AI.", color = KinetiqGray)

                Spacer(modifier = Modifier.height(28.dp))

                SelectionGroup("GOAL", listOf("Hypertrophy", "Strength", "Fat Burn", "Endurance", "Mobility"), selectedGoal) { selectedGoal = it }
                Spacer(modifier = Modifier.height(20.dp))
                
                SelectionGroup("TARGET MUSCLE / SPLIT", listOf("Full Body", "Push (Chest/Delts/Tri)", "Pull (Back/Biceps)", "Legs & Lower", "Core & Abs"), selectedMuscleGroup) { selectedMuscleGroup = it }
                Spacer(modifier = Modifier.height(20.dp))

                SelectionGroup("EXPERIENCE LEVEL", listOf("Beginner", "Intermediate", "Advanced", "Elite"), selectedFitnessLevel) { selectedFitnessLevel = it }
                Spacer(modifier = Modifier.height(20.dp))

                Text("AVAILABLE SESSION TIME", style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = selectedTime.toFloat(),
                    onValueChange = { selectedTime = it.toInt() },
                    valueRange = 15f..120f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = KinetiqLime,
                        activeTrackColor = KinetiqLime,
                        inactiveTrackColor = KinetiqGray.copy(alpha = 0.2f)
                    )
                )
                Text(text = "$selectedTime MIN", color = Color.White, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))
                SelectionGroup("EQUIPMENT AVAILABLE", listOf("Full Gym", "Dumbbells Only", "Bodyweight", "Kettlebell & Bands", "Barbell & Rack"), selectedEquipment) { selectedEquipment = it }

                Spacer(modifier = Modifier.height(20.dp))
                SelectionGroup("WORKOUT STYLE", listOf("Straight Sets", "Supersets", "Circuit Training", "HIIT / Tabata"), selectedWorkoutStyle) { selectedWorkoutStyle = it }

                Spacer(modifier = Modifier.height(20.dp))
                SelectionGroup("JOINT FOCUS / LIMITATIONS", listOf("None", "Low Back Friendly", "Knee Friendly", "Shoulder Friendly", "No Jumping"), selectedLimitations) { selectedLimitations = it }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        viewModel.generateWorkout(
                            goal = selectedGoal, 
                            muscleGroup = selectedMuscleGroup, 
                            time = selectedTime, 
                            equipment = selectedEquipment,
                            fitnessLevel = selectedFitnessLevel,
                            workoutStyle = selectedWorkoutStyle,
                            limitations = selectedLimitations
                        ) {
                            // Workout generated and stored in generatedWorkout StateFlow
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                    shape = MaterialTheme.shapes.medium,
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = KinetiqBlack,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KinetiqBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GENERATE AI WORKOUT", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionGroup(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = KinetiqCharcoal,
                        labelColor = Color.White,
                        selectedContainerColor = KinetiqLime,
                        selectedLabelColor = KinetiqBlack
                    ),
                    border = null
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
