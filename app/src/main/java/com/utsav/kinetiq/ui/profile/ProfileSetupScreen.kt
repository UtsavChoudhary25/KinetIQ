package com.utsav.kinetiq.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utsav.kinetiq.domain.model.UserProfile
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3
    
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("Build Muscle") }
    var experienceLevel by remember { mutableStateOf("Intermediate") }

    val isLoading by viewModel.isLoading
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.profileEvent.collect { event ->
            when (event) {
                is ProfileEvent.Success -> onSetupComplete()
                is ProfileEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = KinetiqBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PROFILE SETUP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp)) },
                actions = {
                    TextButton(onClick = {
                        viewModel.saveProfile(
                            UserProfile(
                                age = age.toIntOrNull() ?: 25,
                                height = height.toFloatOrNull() ?: 175f,
                                weight = weight.toFloatOrNull() ?: 70f,
                                fitnessGoal = fitnessGoal.ifEmpty { "General Fitness" },
                                experienceLevel = experienceLevel.ifEmpty { "Intermediate" },
                                onboardingCompleted = true
                            )
                        )
                    }) {
                        Text("SKIP", color = KinetiqLime, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                .padding(24.dp)
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = KinetiqLime,
                trackColor = KinetiqGray.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                1 -> StepBasicInfo(
                    age = age, onAgeChange = { age = it },
                    height = height, onHeightChange = { height = it },
                    weight = weight, onWeightChange = { weight = it }
                )
                2 -> StepFitnessGoal(
                    fitnessGoal = fitnessGoal, onGoalChange = { fitnessGoal = it }
                )
                3 -> StepExperience(
                    experienceLevel = experienceLevel, onLevelChange = { experienceLevel = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqGray)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BACK", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < totalSteps) {
                            currentStep++
                        } else {
                            viewModel.saveProfile(
                                UserProfile(
                                    age = age.toIntOrNull() ?: 25,
                                    height = height.toFloatOrNull() ?: 175f,
                                    weight = weight.toFloatOrNull() ?: 70f,
                                    fitnessGoal = fitnessGoal.ifEmpty { "General Fitness" },
                                    experienceLevel = experienceLevel.ifEmpty { "Intermediate" },
                                    onboardingCompleted = true
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = KinetiqBlack, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (currentStep == totalSteps) "FINISH" else "NEXT", fontWeight = FontWeight.Black)
                        if (currentStep < totalSteps) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepBasicInfo(age: String, onAgeChange: (String) -> Unit, height: String, onHeightChange: (String) -> Unit, weight: String, onWeightChange: (String) -> Unit) {
    Column {
        Text("YOUR BIOMETRICS", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Input your age, height, and weight to calibrate your AI workout and calorie targets.", color = KinetiqGray, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = age,
            onValueChange = onAgeChange,
            label = { Text("Age (Years)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = height,
            onValueChange = onHeightChange,
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }
}

@Composable
fun StepFitnessGoal(fitnessGoal: String, onGoalChange: (String) -> Unit) {
    val goals = listOf("Build Muscle", "Lose Fat", "Improve Strength", "Endurance", "General Fitness")
    Column {
        Text("WHAT IS YOUR MAIN GOAL?", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Choose your target outcome.", color = KinetiqGray)
        Spacer(modifier = Modifier.height(28.dp))
        goals.forEach { goal ->
            val isSelected = fitnessGoal == goal
            Surface(
                onClick = { onGoalChange(goal) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) KinetiqLime else KinetiqCharcoal,
                contentColor = if (isSelected) KinetiqBlack else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) KinetiqLime else Color.Transparent)
            ) {
                Box(modifier = Modifier.padding(18.dp), contentAlignment = Alignment.CenterStart) {
                    Text(goal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun StepExperience(experienceLevel: String, onLevelChange: (String) -> Unit) {
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    Column {
        Text("EXPERIENCE LEVEL", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Select your training background.", color = KinetiqGray)
        Spacer(modifier = Modifier.height(28.dp))
        levels.forEach { level ->
            val isSelected = experienceLevel == level
            Surface(
                onClick = { onLevelChange(level) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) KinetiqLime else KinetiqCharcoal,
                contentColor = if (isSelected) KinetiqBlack else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) KinetiqLime else Color.Transparent)
            ) {
                Box(modifier = Modifier.padding(18.dp), contentAlignment = Alignment.CenterStart) {
                    Text(level, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun customTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = KinetiqLime,
    unfocusedIndicatorColor = KinetiqGray,
    focusedLabelColor = KinetiqLime,
    unfocusedLabelColor = KinetiqGray
)

