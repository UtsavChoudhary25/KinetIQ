package com.utsav.kinetiq.ui.plans

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utsav.kinetiq.data.local.SpecializedDiscipline
import com.utsav.kinetiq.data.local.SpecializedPlansData
import com.utsav.kinetiq.domain.model.Workout
import com.utsav.kinetiq.ui.recipe.RecipesViewModel
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializedPlansScreen(
    onBackClick: () -> Unit,
    onStartWorkout: (Workout) -> Unit,
    recipesViewModel: RecipesViewModel
) {
    var selectedDiscipline by remember { mutableStateOf(SpecializedDiscipline.ATHLETE) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Workout, 1 = Diet Plan
    var showMealAppliedToast by remember { mutableStateOf(false) }

    val currentPlan = remember(selectedDiscipline) {
        SpecializedPlansData.getPlan(selectedDiscipline)
    }

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "SPECIALIZED DISCIPLINE PLANS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Discipline Selector Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SpecializedDiscipline.entries.toTypedArray()) { discipline ->
                    val isSelected = selectedDiscipline == discipline
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDiscipline = discipline },
                        label = {
                            Text(
                                text = "${discipline.iconEmoji} ${discipline.title}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = KinetiqCharcoal,
                            labelColor = KinetiqGray,
                            selectedContainerColor = KinetiqLime,
                            selectedLabelColor = KinetiqBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent,
                            selectedBorderColor = KinetiqLime
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Discipline Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        colors = listOf(KinetiqLime.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 12.dp)
                        ) {
                            Text(currentPlan.discipline.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentPlan.discipline.title.uppercase(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentPlan.discipline.tagline,
                                    style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            color = KinetiqLime.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = KinetiqLime,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentPlan.targetCalories} KCAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = KinetiqLime,
                                        fontWeight = FontWeight.Black
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentPlan.focusDescription,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = KinetiqGray,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Macro Distribution Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("PROTEIN ${currentPlan.proteinPercent}%", color = KinetiqLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("CARBS ${currentPlan.carbsPercent}%", color = Color(0xFF2196F3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("FATS ${currentPlan.fatPercent}%", color = Color(0xFFFFC107), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    ) {
                        Box(modifier = Modifier.weight(currentPlan.proteinPercent / 100f).fillMaxHeight().background(KinetiqLime))
                        Box(modifier = Modifier.weight(currentPlan.carbsPercent / 100f).fillMaxHeight().background(Color(0xFF2196F3)))
                        Box(modifier = Modifier.weight(currentPlan.fatPercent / 100f).fillMaxHeight().background(Color(0xFFFFC107)))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-Tab Switcher (Workout vs Diet)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = KinetiqCharcoal,
                contentColor = KinetiqLime,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = KinetiqLime
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WORKOUT ROUTINE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = KinetiqLime,
                    unselectedContentColor = KinetiqGray
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DIET & NUTRITION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = KinetiqLime,
                    unselectedContentColor = KinetiqGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            if (selectedTab == 0) {
                // WORKOUT ROUTINE TAB
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = currentPlan.workout.title.uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Black)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentPlan.workout.aiRationale,
                                style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray, lineHeight = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(currentPlan.workout.exercises) { ex ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(ex.exercise.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Surface(color = KinetiqBlack, shape = RoundedCornerShape(8.dp)) {
                                            Text(ex.exercise.muscleGroup, color = KinetiqLime, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(ex.exercise.instructions, color = KinetiqGray, fontSize = 12.sp, lineHeight = 16.sp)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ex.sets.forEachIndexed { idx, set ->
                                            Surface(color = KinetiqBlack, shape = RoundedCornerShape(6.dp)) {
                                                Text("Set ${idx + 1}: ${set.reps} reps", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onStartWorkout(currentPlan.workout) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("START THIS WORKOUT NOW", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                // DIET & NUTRITION TAB
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentPlan.meals) { meal ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${meal.mealType.uppercase()}: ${meal.title}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${meal.calories} kcal", color = KinetiqLime, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(meal.description, color = KinetiqGray, fontSize = 12.sp)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text("Protein: ${meal.proteinGrams}g", color = KinetiqLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Carbs: ${meal.carbsGrams}g", color = Color(0xFF2196F3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Fats: ${meal.fatGrams}g", color = Color(0xFFFFC107), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Ingredients: ${meal.ingredients.joinToString(", ")}", color = KinetiqGray, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (showMealAppliedToast) {
                        Text("✓ All meals applied to Monday's Meal Planner!", color = KinetiqLime, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Button(
                        onClick = {
                            currentPlan.meals.forEach { meal ->
                                recipesViewModel.addMealPlan(
                                    day = "Monday",
                                    mealType = meal.mealType,
                                    recipe = com.utsav.kinetiq.domain.model.Recipe(
                                        id = "sp_${System.currentTimeMillis()}",
                                        title = meal.title,
                                        description = meal.description,
                                        category = "Specialized Plan",
                                        fitnessGoal = currentPlan.discipline.title,
                                        prepTimeMinutes = 10,
                                        cookTimeMinutes = 15,
                                        calories = meal.calories,
                                        proteinGrams = meal.proteinGrams,
                                        carbsGrams = meal.carbsGrams,
                                        fatGrams = meal.fatGrams,
                                        servings = 1,
                                        difficulty = "Easy",
                                        imageUrl = "",
                                        ingredients = meal.ingredients,
                                        instructions = listOf(meal.description)
                                    )
                                )
                            }
                            showMealAppliedToast = true
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KinetiqCharcoal, contentColor = KinetiqLime),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.4f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("APPLY MEAL PLAN TO CALENDAR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
