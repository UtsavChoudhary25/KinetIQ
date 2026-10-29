package com.utsav.kinetiq.ui.recipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.utsav.kinetiq.domain.model.Recipe
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBackClick: () -> Unit,
    viewModel: RecipesViewModel
) {
    val recipe = remember(recipeId) { viewModel.getRecipeById(recipeId) }

    if (recipe == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KinetiqBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Recipe not found", color = Color.White)
        }
        return
    }

    var isFavorite by remember { mutableStateOf(recipe.isFavorite) }
    val checkedIngredients = remember { mutableStateMapOf<Int, Boolean>() }
    val checkedSteps = remember { mutableStateMapOf<Int, Boolean>() }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = KinetiqBlack,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(KinetiqBlack.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(recipe.id)
                            isFavorite = !isFavorite
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(KinetiqBlack.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    KinetiqBlack
                                )
                            )
                        )
                )

                // Category & Fitness Goal Pills at bottom of image
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = KinetiqLime,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = recipe.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqBlack,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Surface(
                        color = KinetiqCharcoal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "🎯 ${recipe.fitnessGoal.uppercase()}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Recipe Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KinetiqGray,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(KinetiqCharcoal)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    QuickStatItem(icon = Icons.Default.Schedule, label = "Prep", value = "${recipe.prepTimeMinutes} mins")
                    QuickStatItem(icon = Icons.Default.SoupKitchen, label = "Cook", value = "${recipe.cookTimeMinutes} mins")
                    QuickStatItem(icon = Icons.Default.Restaurant, label = "Servings", value = "${recipe.servings}")
                    QuickStatItem(icon = Icons.Default.Speed, label = "Level", value = recipe.difficulty)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Macro Breakdown Card
                Text(
                    text = "MACRONUTRIENTS (PER SERVING)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = KinetiqCharcoal),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${recipe.calories} Calories",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }

                            Text(
                                text = "High Satiety",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = KinetiqLime,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Macro Bars
                        MacroProgressItem(
                            label = "Protein",
                            amountGrams = recipe.proteinGrams,
                            color = KinetiqLime,
                            percentage = (recipe.proteinGrams / 60f).coerceAtMost(1f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MacroProgressItem(
                            label = "Carbohydrates",
                            amountGrams = recipe.carbsGrams,
                            color = Color(0xFF2196F3),
                            percentage = (recipe.carbsGrams / 80f).coerceAtMost(1f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MacroProgressItem(
                            label = "Healthy Fats",
                            amountGrams = recipe.fatGrams,
                            color = Color(0xFFFFC107),
                            percentage = (recipe.fatGrams / 40f).coerceAtMost(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Ingredients Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INGREDIENTS (${recipe.ingredients.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Tap to check off",
                        style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recipe.ingredients.forEachIndexed { index, ingredient ->
                        val isChecked = checkedIngredients[index] == true
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    checkedIngredients[index] = !isChecked
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) KinetiqCharcoal.copy(alpha = 0.5f) else KinetiqCharcoal
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isChecked) KinetiqLime else KinetiqGray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isChecked) KinetiqGray else Color.White,
                                        fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Step-by-Step Instructions Section
                Text(
                    text = "STEP-BY-STEP INSTRUCTIONS",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    recipe.instructions.forEachIndexed { index, step ->
                        val isStepDone = checkedSteps[index] == true
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    checkedSteps[index] = !isStepDone
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isStepDone) KinetiqCharcoal.copy(alpha = 0.4f) else KinetiqCharcoal
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = if (isStepDone) null else androidx.compose.foundation.BorderStroke(1.dp, KinetiqGray.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    color = if (isStepDone) KinetiqLime else KinetiqLime.copy(alpha = 0.15f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isStepDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = KinetiqBlack,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = KinetiqLime,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isStepDone) KinetiqGray else Color.White,
                                        lineHeight = 22.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Fitness Coach Note
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = KinetiqLime.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = KinetiqLime,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "NUTRITIONIST TIP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = KinetiqLime,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "This recipe provides ${recipe.proteinGrams}g of complete protein to optimize muscle protein synthesis and support your ${recipe.fitnessGoal.lowercase()} goals.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                var showMealPlanDialog by remember { mutableStateOf(false) }
                var showAddedToast by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.addIngredientsToShoppingList(recipe.ingredients, recipe.title)
                            showAddedToast = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinetiqCharcoal,
                            contentColor = KinetiqLime
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.3f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Grocery List", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { showMealPlanDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KinetiqLime,
                            contentColor = KinetiqBlack
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Plan Meal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (showAddedToast) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "✓ Ingredients added to Grocery List!",
                        style = MaterialTheme.typography.labelSmall.copy(color = KinetiqLime, fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                if (showMealPlanDialog) {
                    var selectedDay by remember { mutableStateOf("Monday") }
                    var selectedMealSlot by remember { mutableStateOf("Lunch") }
                    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                    val mealSlots = listOf("Breakfast", "Lunch", "Dinner", "Snack")

                    AlertDialog(
                        onDismissRequest = { showMealPlanDialog = false },
                        containerColor = KinetiqCharcoal,
                        title = {
                            Text("Add to Weekly Meal Plan", color = Color.White, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Select Day:", color = KinetiqGray, style = MaterialTheme.typography.bodySmall)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(days) { day ->
                                        FilterChip(
                                            selected = selectedDay == day,
                                            onClick = { selectedDay = day },
                                            label = { Text(day, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = KinetiqLime,
                                                selectedLabelColor = KinetiqBlack,
                                                containerColor = KinetiqBlack,
                                                labelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Text("Select Meal Slot:", color = KinetiqGray, style = MaterialTheme.typography.bodySmall)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(mealSlots) { slot ->
                                        FilterChip(
                                            selected = selectedMealSlot == slot,
                                            onClick = { selectedMealSlot = slot },
                                            label = { Text(slot, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = KinetiqLime,
                                                selectedLabelColor = KinetiqBlack,
                                                containerColor = KinetiqBlack,
                                                labelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.addMealPlan(selectedDay, selectedMealSlot, recipe)
                                    showMealPlanDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack)
                            ) {
                                Text("Save Plan", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showMealPlanDialog = false }) {
                                Text("Cancel", color = KinetiqGray)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun QuickStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KinetiqLime,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray)
        )
    }
}

@Composable
fun MacroProgressItem(
    label: String,
    amountGrams: Int,
    color: Color,
    percentage: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = KinetiqGray,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = "${amountGrams}g",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = KinetiqBlack
        )
    }
}
