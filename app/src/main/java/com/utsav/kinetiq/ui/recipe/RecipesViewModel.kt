package com.utsav.kinetiq.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utsav.kinetiq.data.local.LocalRecipeRepository
import com.utsav.kinetiq.data.local.MealPlanEntry
import com.utsav.kinetiq.data.local.MealPlannerPrefs
import com.utsav.kinetiq.data.local.ShoppingListItem
import com.utsav.kinetiq.data.local.ShoppingListPrefs
import com.utsav.kinetiq.data.remote.GeminiRepository
import com.utsav.kinetiq.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipesViewModel(
    private val repository: LocalRecipeRepository,
    private val shoppingListPrefs: ShoppingListPrefs? = null,
    private val mealPlannerPrefs: MealPlannerPrefs? = null,
    private val geminiRepository: GeminiRepository? = null
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedGoal = MutableStateFlow("All Goals")
    val selectedGoal: StateFlow<String> = _selectedGoal.asStateFlow()

    private val _isGeneratingRecipe = MutableStateFlow(false)
    val isGeneratingRecipe: StateFlow<Boolean> = _isGeneratingRecipe.asStateFlow()

    private val _aiGeneratedRecipe = MutableStateFlow<Recipe?>(null)
    val aiGeneratedRecipe: StateFlow<Recipe?> = _aiGeneratedRecipe.asStateFlow()

    val shoppingList: StateFlow<List<ShoppingListItem>> = shoppingListPrefs?.shoppingList
        ?: MutableStateFlow(emptyList())

    val mealPlans: StateFlow<List<MealPlanEntry>> = mealPlannerPrefs?.mealPlans
        ?: MutableStateFlow(emptyList())

    val recipes: StateFlow<List<Recipe>> = combine(
        repository.recipes,
        _searchQuery,
        _selectedCategory,
        _selectedGoal
    ) { allRecipes, query, category, goal ->
        val listWithAi = if (_aiGeneratedRecipe.value != null && allRecipes.none { it.id == _aiGeneratedRecipe.value?.id }) {
            listOf(_aiGeneratedRecipe.value!!) + allRecipes
        } else {
            allRecipes
        }

        listWithAi.filter { recipe ->
            val matchesQuery = query.isBlank() ||
                    recipe.title.contains(query, ignoreCase = true) ||
                    recipe.description.contains(query, ignoreCase = true) ||
                    recipe.ingredients.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = when (category) {
                "All" -> true
                "Favorites" -> recipe.isFavorite
                "Quick <15m" -> (recipe.prepTimeMinutes + recipe.cookTimeMinutes) <= 15
                "Indian" -> recipe.category.equals("Indian", ignoreCase = true) || recipe.title.contains("Indian", ignoreCase = true) || recipe.description.contains("Indian", ignoreCase = true)
                "High Protein" -> recipe.category.equals("High Protein", ignoreCase = true) || recipe.proteinGrams >= 25
                "Low Carb" -> recipe.category.equals("Low Carb", ignoreCase = true) || recipe.carbsGrams <= 20
                "Vegan" -> recipe.category.equals("Vegan", ignoreCase = true) || recipe.description.contains("Vegan", ignoreCase = true) || recipe.ingredients.any { it.contains("Vegan", ignoreCase = true) || it.contains("Tofu", ignoreCase = true) || it.contains("Tempeh", ignoreCase = true) }
                "Pre-Workout" -> recipe.category.equals("Pre-Workout", ignoreCase = true) || recipe.category.contains("Pre", ignoreCase = true) || recipe.title.contains("Pre-Workout", ignoreCase = true)
                "Post-Workout" -> recipe.category.equals("Post-Workout", ignoreCase = true) || recipe.category.contains("Post", ignoreCase = true) || recipe.title.contains("Post-Workout", ignoreCase = true)
                else -> recipe.category.equals(category, ignoreCase = true)
            }

            val matchesGoal = when (goal) {
                "All Goals" -> true
                else -> recipe.fitnessGoal.equals(goal, ignoreCase = true)
            }

            matchesQuery && matchesCategory && matchesGoal
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onGoalSelect(goal: String) {
        _selectedGoal.value = goal
    }

    fun toggleFavorite(recipeId: String) {
        repository.toggleFavorite(recipeId)
    }

    fun getRecipeById(id: String): Recipe? {
        if (_aiGeneratedRecipe.value?.id == id) return _aiGeneratedRecipe.value
        return repository.recipes.value.find { it.id == id }
    }

    fun generateRecipeFromIngredients(ingredientsText: String) {
        if (ingredientsText.isBlank() || geminiRepository == null) return
        viewModelScope.launch {
            _isGeneratingRecipe.value = true
            val recipe = geminiRepository.generateRecipeFromIngredients(ingredientsText)
            if (recipe != null) {
                _aiGeneratedRecipe.value = recipe
            }
            _isGeneratingRecipe.value = false
        }
    }

    fun addIngredientsToShoppingList(ingredients: List<String>, recipeTitle: String) {
        shoppingListPrefs?.addItems(ingredients, recipeTitle)
    }

    fun toggleShoppingItem(id: String) {
        shoppingListPrefs?.toggleItem(id)
    }

    fun removeShoppingItem(id: String) {
        shoppingListPrefs?.removeItem(id)
    }

    fun clearCheckedShoppingItems() {
        shoppingListPrefs?.clearChecked()
    }

    fun addMealPlan(day: String, mealType: String, recipe: Recipe) {
        mealPlannerPrefs?.addMealPlan(
            dayOfWeek = day,
            mealType = mealType,
            recipeId = recipe.id,
            recipeTitle = recipe.title,
            calories = recipe.calories,
            proteinGrams = recipe.proteinGrams,
            carbsGrams = recipe.carbsGrams,
            fatGrams = recipe.fatGrams
        )
    }

    fun removeMealPlan(id: String) {
        mealPlannerPrefs?.removeMealPlan(id)
    }

    companion object {
        val categories = listOf(
            "All",
            "Indian",
            "Favorites",
            "Quick <15m",
            "High Protein",
            "Low Carb",
            "Pre-Workout",
            "Post-Workout",
            "Vegan"
        )

        val fitnessGoals = listOf(
            "All Goals",
            "Muscle Growth",
            "Fat Loss",
            "Lean Cut",
            "Energy Booster",
            "Recovery"
        )

        fun provideFactory(
            repository: LocalRecipeRepository,
            shoppingListPrefs: ShoppingListPrefs? = null,
            mealPlannerPrefs: MealPlannerPrefs? = null,
            geminiRepository: GeminiRepository? = null
        ): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecipesViewModel(repository, shoppingListPrefs, mealPlannerPrefs, geminiRepository) as T
                }
            }
        }
    }
}
