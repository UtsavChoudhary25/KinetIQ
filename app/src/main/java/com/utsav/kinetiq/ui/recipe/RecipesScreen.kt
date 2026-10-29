package com.utsav.kinetiq.ui.recipe

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.utsav.kinetiq.domain.model.Recipe
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime

@Composable
fun RecipesScreen(
    onRecipeClick: (String) -> Unit,
    viewModel: RecipesViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedGoal by viewModel.selectedGoal.collectAsState()
    val recipes by viewModel.recipes.collectAsState()

    Scaffold(
        containerColor = KinetiqBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HEALTHY RECIPES",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = KinetiqLime,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "FITNESS NUTRITION",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                }
                
                Surface(
                    color = KinetiqLime.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "${recipes.size} MEALS",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqLime,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search ingredients or meal names...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = KinetiqGray)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = KinetiqLime
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = KinetiqGray
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = KinetiqCharcoal,
                    unfocusedContainerColor = KinetiqCharcoal,
                    focusedBorderColor = KinetiqLime,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            var showFridgeAiDialog by remember { mutableStateOf(false) }
            var showShoppingListSheet by remember { mutableStateOf(false) }
            var showMealPlannerSheet by remember { mutableStateOf(false) }

            // Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { showFridgeAiDialog = true },
                    modifier = Modifier.weight(1f),
                    color = KinetiqLime.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KinetiqLime.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KinetiqLime, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fridge AI", color = KinetiqLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    onClick = { showShoppingListSheet = true },
                    modifier = Modifier.weight(1f),
                    color = KinetiqCharcoal,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KinetiqGray.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Grocery", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    onClick = { showMealPlannerSheet = true },
                    modifier = Modifier.weight(1f),
                    color = KinetiqCharcoal,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, KinetiqGray.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Planner", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(RecipesViewModel.categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelect(category) },
                        label = {
                            Text(
                                text = category,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Goal Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(RecipesViewModel.fitnessGoals) { goal ->
                    val isSelected = selectedGoal == goal
                    SuggestionChip(
                        onClick = { viewModel.onGoalSelect(goal) },
                        label = {
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) KinetiqLime else KinetiqGray
                                )
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (isSelected) KinetiqLime.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = if (isSelected) KinetiqLime else KinetiqGray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recipes List
            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = KinetiqGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No fitness recipes found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Try clearing search or changing filters",
                            style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = recipes,
                        key = { it.id }
                    ) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) }
                        )
                    }
                }
            }

            if (showFridgeAiDialog) {
                FridgeAiDialog(
                    onDismiss = { showFridgeAiDialog = false },
                    viewModel = viewModel
                )
            }

            if (showShoppingListSheet) {
                ShoppingListBottomSheet(
                    onDismiss = { showShoppingListSheet = false },
                    viewModel = viewModel
                )
            }

            if (showMealPlannerSheet) {
                MealPlannerBottomSheet(
                    onDismiss = { showMealPlannerSheet = false },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = KinetiqLime.copy(alpha = 0.15f)
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
        Column {
            // Image with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Bottom Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = KinetiqBlack.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = recipe.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = KinetiqLime,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(KinetiqBlack.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (recipe.isFavorite) Color.Red else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom Overlay Badge on Image
                Surface(
                    color = KinetiqLime.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 0.dp, topEnd = 12.dp, bottomEnd = 0.dp, topStart = 12.dp),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "🎯 ${recipe.fitnessGoal.uppercase()}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = KinetiqBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Recipe Details Body
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = KinetiqGray,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Macros & Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Protein Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = KinetiqLime,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.proteinGrams}g Protein",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Calories Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.calories} kcal",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Prep Time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = KinetiqGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.prepTimeMinutes + recipe.cookTimeMinutes}m",
                            style = MaterialTheme.typography.labelSmall.copy(color = KinetiqGray)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeAiDialog(
    onDismiss: () -> Unit,
    viewModel: RecipesViewModel
) {
    var ingredientsInput by remember { mutableStateOf("") }
    val isGenerating by viewModel.isGeneratingRecipe.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = KinetiqCharcoal,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KinetiqLime)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fridge to Plate AI", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Type what ingredients you currently have in your fridge (e.g. paneer, tomatoes, eggs, spinach):",
                    style = MaterialTheme.typography.bodySmall.copy(color = KinetiqGray)
                )

                OutlinedTextField(
                    value = ingredientsInput,
                    onValueChange = { ingredientsInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. chicken breast, broccoli, garlic, olive oil", color = KinetiqGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KinetiqBlack,
                        unfocusedContainerColor = KinetiqBlack,
                        focusedBorderColor = KinetiqLime,
                        unfocusedBorderColor = KinetiqGray.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (isGenerating) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = KinetiqLime, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Crafting healthy recipe...", color = KinetiqLime, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.generateRecipeFromIngredients(ingredientsInput)
                    onDismiss()
                },
                enabled = ingredientsInput.isNotBlank() && !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = KinetiqLime, contentColor = KinetiqBlack)
            ) {
                Text("Generate Recipe", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KinetiqGray)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListBottomSheet(
    onDismiss: () -> Unit,
    viewModel: RecipesViewModel
) {
    val items by viewModel.shoppingList.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = KinetiqCharcoal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = KinetiqLime)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GROCERY SHOPPING LIST", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Black))
                }

                if (items.any { it.isChecked }) {
                    TextButton(onClick = { viewModel.clearCheckedShoppingItems() }) {
                        Text("Clear Checked", color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No items in your grocery list yet.\nAdd ingredients from recipe detail screens!", color = KinetiqGray, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.6f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = KinetiqBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = item.isChecked,
                                        onCheckedChange = { viewModel.toggleShoppingItem(item.id) },
                                        colors = CheckboxDefaults.colors(checkedColor = KinetiqLime, checkmarkColor = KinetiqBlack)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.text,
                                            color = if (item.isChecked) KinetiqGray else Color.White,
                                            fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium
                                        )
                                        if (item.recipeTitle.isNotBlank()) {
                                            Text(
                                                text = "From: ${item.recipeTitle}",
                                                color = KinetiqLime,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                IconButton(onClick = { viewModel.removeShoppingItem(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = KinetiqGray, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerBottomSheet(
    onDismiss: () -> Unit,
    viewModel: RecipesViewModel
) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = KinetiqCharcoal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = KinetiqLime)
                Spacer(modifier = Modifier.width(8.dp))
                Text("WEEKLY MEAL PLANNER", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Black))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.7f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(days) { day ->
                    val dayPlans = mealPlans.filter { it.dayOfWeek.equals(day, ignoreCase = true) }
                    val totalCalories = dayPlans.sumOf { it.calories }
                    val totalProtein = dayPlans.sumOf { it.proteinGrams }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = KinetiqBlack),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(day, color = KinetiqLime, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("$totalCalories kcal | ${totalProtein}g Protein", color = KinetiqGray, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (dayPlans.isEmpty()) {
                                Text("No meals planned for $day", color = KinetiqGray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    dayPlans.forEach { plan ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().background(KinetiqCharcoal, RoundedCornerShape(8.dp)).padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("[${plan.mealType}] ${plan.recipeTitle}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                Text("${plan.calories} kcal • ${plan.proteinGrams}g Protein", color = KinetiqGray, fontSize = 10.sp)
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeMealPlan(plan.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = KinetiqGray, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
