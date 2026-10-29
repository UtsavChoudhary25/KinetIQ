package com.utsav.kinetiq.data.local

import android.content.Context
import com.utsav.kinetiq.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalRecipeRepository(context: Context) {

    private val prefs = context.getSharedPreferences("kinetiq_recipes_prefs", Context.MODE_PRIVATE)

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    init {
        loadRecipes()
    }

    private fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet("favorite_recipe_ids", emptySet()) ?: emptySet()
    }

    fun toggleFavorite(recipeId: String) {
        val currentFavorites = getFavoriteIds().toMutableSet()
        if (currentFavorites.contains(recipeId)) {
            currentFavorites.remove(recipeId)
        } else {
            currentFavorites.add(recipeId)
        }
        prefs.edit().putStringSet("favorite_recipe_ids", currentFavorites).apply()

        _recipes.value = _recipes.value.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isFavorite = currentFavorites.contains(recipeId))
            } else {
                recipe
            }
        }
    }

    private fun loadRecipes() {
        val favoriteIds = getFavoriteIds()
        val allRecipes = initialFitnessRecipes.map { recipe ->
            recipe.copy(isFavorite = favoriteIds.contains(recipe.id))
        }
        _recipes.value = allRecipes
    }

    companion object {
        val initialFitnessRecipes = listOf(
            // ==========================================
            // --- 1. INDIAN FITNESS RECIPES (15) ---
            // ==========================================
            Recipe(
                id = "recipe_ind_1",
                title = "High-Protein Tandoori Paneer Tikka",
                description = "Classic Indian grilled cottage cheese skewers marinated in spiced hung curd. Packed with calcium and slow-digesting casein protein.",
                category = "Indian",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 20,
                cookTimeMinutes = 15,
                calories = 380,
                proteinGrams = 28,
                carbsGrams = 12,
                fatGrams = 18,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "250g Low-fat Paneer, cut into 1-inch cubes",
                    "1/2 cup Hung Curd (Greek Yogurt)",
                    "1 tbsp Ginger-garlic paste",
                    "1 tsp Kashmiri red chili powder & Garam masala",
                    "1 tsp Kasoori methi (dried fenugreek)",
                    "1 Bell pepper & 1 Red onion, cubed",
                    "1 tbsp Lemon juice & 1 tsp Mustard oil"
                ),
                instructions = listOf(
                    "In a bowl, whisk hung curd, ginger-garlic paste, red chili powder, garam masala, kasoori methi, lemon juice, mustard oil, and salt.",
                    "Add paneer cubes, bell peppers, and onion. Gently coat and marinate for 20-30 minutes.",
                    "Thread paneer and vegetables onto wooden or metal skewers.",
                    "Air-fry at 390°F (200°C) for 12-15 minutes or grill in a hot skillet until edges are golden and lightly charred.",
                    "Sprinkle chaat masala and fresh lemon juice before serving warm with mint chutney."
                )
            ),
            Recipe(
                id = "recipe_ind_2",
                title = "Fit Indian Chicken Tikka Masala",
                description = "Lean grilled chicken breast in a rich tomato, ginger, and aromatic spiced gravy without heavy cream or butter.",
                category = "Indian",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 15,
                cookTimeMinutes = 20,
                calories = 450,
                proteinGrams = 44,
                carbsGrams = 18,
                fatGrams = 12,
                servings = 2,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "350g Skinless Chicken breast, cut into bites",
                    "1/2 cup Plain low-fat yogurt",
                    "1.5 cups Fresh tomato puree",
                    "1 Onion, finely diced",
                    "1 tbsp Ginger-garlic paste",
                    "1 tsp Cumin, Coriander, Turmeric & Garam Masala"
                ),
                instructions = listOf(
                    "Marinate chicken in yogurt, half ginger-garlic paste, chili powder, and salt for 15 mins.",
                    "Pan-sear chicken in skillet over high heat for 6-8 minutes until cooked through; set aside.",
                    "Heat oil in same skillet. Sauté diced onions and remaining ginger-garlic paste until golden.",
                    "Pour in tomato puree and dry spices. Simmer gravy on medium heat for 8-10 minutes.",
                    "Toss grilled chicken tikka pieces into gravy and cook for 3 minutes."
                )
            ),
            Recipe(
                id = "recipe_ind_3",
                title = "Paneer Bhurji with Multigrain Roti",
                description = "Scrambled Indian cottage cheese tossed with green chilies, onions, tomatoes, and turmeric for quick post-workout recovery.",
                category = "Indian",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 410,
                proteinGrams = 32,
                carbsGrams = 32,
                fatGrams = 14,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Low-fat Paneer, crumbled",
                    "1 Medium Onion & 1 Tomato, finely chopped",
                    "1 Green chili, minced",
                    "1 tsp Cumin seeds & 1/2 tsp Turmeric",
                    "1 tsp Desi Ghee",
                    "1 Multigrain Roti"
                ),
                instructions = listOf(
                    "Heat ghee in non-stick skillet over medium heat and add cumin seeds.",
                    "Sauté chopped onions and green chilies until translucent.",
                    "Add chopped tomatoes, turmeric, salt, and red chili powder.",
                    "Add crumbled low-fat paneer and mix gently for 2-3 minutes.",
                    "Garnish with chopped coriander and serve hot alongside multigrain roti."
                )
            ),
            Recipe(
                id = "recipe_ind_4",
                title = "Protein Moong Dal Chilla with Stuffed Cottage Cheese",
                description = "Savory, crispy Indian crepes made from blended green/yellow moong lentil batter filled with seasoned paneer.",
                category = "Indian",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 15,
                cookTimeMinutes = 10,
                calories = 320,
                proteinGrams = 24,
                carbsGrams = 34,
                fatGrams = 8,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Yellow Moong Dal (soaked 2 hours & ground into batter)",
                    "100g Low-fat Paneer, grated",
                    "1/2 cup Baby spinach, finely chopped",
                    "1 tsp Ginger & Green chili paste",
                    "1/2 tsp Cumin powder & Hing"
                ),
                instructions = listOf(
                    "Blend soaked moong dal with ginger, green chili, hing, salt, and water into smooth batter.",
                    "In a bowl, mix grated paneer, chopped spinach, cumin powder, and salt for filling.",
                    "Pour a ladle of batter on hot tawa and spread circularly into a thin crepe.",
                    "Cook 2 minutes until golden, flip, and place paneer filling in center.",
                    "Fold chilla over filling and serve hot with green mint chutney."
                )
            ),
            Recipe(
                id = "recipe_ind_5",
                title = "Masala Egg Bhurji (3 Whites + 1 Whole Egg)",
                description = "High-protein spicy Indian scrambled eggs packed with onions, tomatoes, chilies, and aromatic spices.",
                category = "Indian",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 5,
                cookTimeMinutes = 8,
                calories = 260,
                proteinGrams = 26,
                carbsGrams = 8,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "3 Egg Whites + 1 Whole Large Egg",
                    "1/2 Medium Onion & 1 Tomato, diced",
                    "1 Green chili, chopped",
                    "1/2 tsp Turmeric & Garam masala",
                    "1 tsp Olive oil or Ghee"
                ),
                instructions = listOf(
                    "Whisk 3 egg whites and 1 whole egg in a bowl with salt and black pepper.",
                    "Heat oil in skillet. Sauté diced onions and green chili for 2 minutes.",
                    "Add tomatoes, turmeric, and garam masala. Cook for 2 minutes until soft.",
                    "Pour beaten egg mixture into skillet and scramble continuously on low-medium heat."
                )
            ),
            Recipe(
                id = "recipe_ind_6",
                title = "Protein Soya Chunk Curry with Jeera Rice",
                description = "Vegetarian muscle builder featuring high-fiber soya chunks simmered in a spiced onion-tomato gravy.",
                category = "Indian",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 15,
                cookTimeMinutes = 20,
                calories = 430,
                proteinGrams = 40,
                carbsGrams = 46,
                fatGrams = 8,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Soya chunks (Nutrela), boiled & thoroughly squeezed",
                    "1 cup Cooked Brown Basmati Rice",
                    "1 cup Tomato-onion gravy base",
                    "1 tbsp Ginger-garlic paste",
                    "1 tsp Cumin seeds, Turmeric & Garam Masala"
                ),
                instructions = listOf(
                    "Boil soya chunks in salted water for 8 minutes, drain, and squeeze dry.",
                    "Heat oil in pan, add cumin seeds, and sauté onion-tomato gravy base with ginger-garlic paste.",
                    "Add spices and squeezed soya chunks. Sauté for 5 minutes.",
                    "Add 1 cup water, cover, and simmer for 8 minutes until gravy thickens."
                )
            ),
            Recipe(
                id = "recipe_ind_7",
                title = "Healthy Fit Palak Paneer",
                description = "Nutrient-rich spinach puree cooked with garlic, mild spices, and low-fat cottage cheese cubes.",
                category = "Indian",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 15,
                cookTimeMinutes = 15,
                calories = 350,
                proteinGrams = 26,
                carbsGrams = 14,
                fatGrams = 16,
                servings = 2,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Low-fat Paneer, cubed",
                    "3 cups Fresh spinach leaves (palak)",
                    "4 cloves Garlic, finely minced",
                    "1 Green chili & 1/2 inch Ginger",
                    "1 tsp Cumin powder & Garam masala"
                ),
                instructions = listOf(
                    "Blanch spinach leaves in boiling water for 2 minutes, then shock in ice water.",
                    "Blend blanched spinach, ginger, and green chili into smooth green puree.",
                    "Sauté minced garlic in ghee, pour spinach puree, and simmer for 5 minutes.",
                    "Gently add paneer cubes and cook for 3 minutes."
                )
            ),
            Recipe(
                id = "recipe_ind_8",
                title = "Steamed Protein Oats Idli with Sambar",
                description = "Instant South Indian steamed oat cakes tempered with mustard seeds, curry leaves, and served with lentil sambar.",
                category = "Indian",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 15,
                cookTimeMinutes = 12,
                calories = 310,
                proteinGrams = 18,
                carbsGrams = 44,
                fatGrams = 6,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Rolled Oats (ground into coarse flour)",
                    "1/2 cup Low-fat Curd (Yogurt)",
                    "1/4 cup Grated carrots & green peas",
                    "1 tsp Mustard seeds & Curry leaves",
                    "1 cup Mixed Vegetable Sambar"
                ),
                instructions = listOf(
                    "Dry roast ground oat flour in pan for 3 minutes; let cool.",
                    "Mix roasted oat flour, curd, tempered veggies, and water into thick batter.",
                    "Pour batter into greased idli molds and steam for 10-12 minutes.",
                    "Serve fluffy hot oats idlis with hot vegetable sambar."
                )
            ),
            Recipe(
                id = "recipe_ind_9",
                title = "Amritsari Chana Masala (High Fiber)",
                description = "Protein and fiber rich North Indian chickpea curry seasoned with amchur, cumin, and garam masala.",
                category = "Indian",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 15,
                cookTimeMinutes = 25,
                calories = 340,
                proteinGrams = 18,
                carbsGrams = 48,
                fatGrams = 7,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 cups White Chickpeas (Kabuli Chana), boiled",
                    "1 Onion & 2 Tomatoes, puréed",
                    "1 tbsp Ginger-garlic paste",
                    "1 tbsp Chana Masala powder & Amchur"
                ),
                instructions = listOf(
                    "Heat oil in pot, add cumin seeds and ginger-garlic paste.",
                    "Sauté puréed onions and tomatoes until oil separates.",
                    "Add chana masala powder, amchur, and boiled chickpeas.",
                    "Simmer on low-medium heat for 15 minutes."
                )
            ),
            Recipe(
                id = "recipe_ind_10",
                title = "Spiced Peri Peri Roasted Makhana",
                description = "Crunchy, low-calorie popped lotus seeds roasted in ghee with peri-peri & Indian chaat spices.",
                category = "Indian",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 5,
                cookTimeMinutes = 8,
                calories = 180,
                proteinGrams = 6,
                carbsGrams = 28,
                fatGrams = 4,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "2 cups Phool Makhana (Lotus seeds)",
                    "1 tsp Desi Ghee",
                    "1 tsp Peri Peri seasoning mix",
                    "1/2 tsp Chaat masala & Black salt"
                ),
                instructions = listOf(
                    "Slow-roast makhana in 1 tsp ghee for 7-8 minutes until super crispy.",
                    "Immediately sprinkle peri peri seasoning and chaat masala.",
                    "Toss thoroughly to coat evenly and cool."
                )
            ),
            Recipe(
                id = "recipe_ind_11",
                title = "High-Protein Kala Chana Chaat",
                description = "Nutrient-dense black chickpea snack tossed with cucumbers, tomatoes, green chilies, and lemon juice.",
                category = "Indian",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 5,
                calories = 220,
                proteinGrams = 14,
                carbsGrams = 32,
                fatGrams = 3,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 cups Boiled Kala Chana (Black chickpeas)",
                    "1/2 Cucumber & 1 Tomato, finely diced",
                    "1/2 Red onion & 1 Green chili, minced",
                    "1 tsp Chaat masala & Lemon juice"
                ),
                instructions = listOf(
                    "In a large bowl, combine boiled kala chana, cucumber, tomato, onion, and chili.",
                    "Add chaat masala, black salt, and lemon juice.",
                    "Mix well and serve fresh."
                )
            ),
            Recipe(
                id = "recipe_ind_12",
                title = "Boiled Egg White Chaat with Mint Chutney",
                description = "Ultra low-fat, 22g protein fitness snack featuring quartered hard-boiled egg whites topped with spicy mint chutney.",
                category = "Indian",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 5,
                cookTimeMinutes = 8,
                calories = 160,
                proteinGrams = 22,
                carbsGrams = 6,
                fatGrams = 5,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "5 Large Hard-Boiled Eggs (use 5 Whites + 1 Yolk)",
                    "2 tbsp Fresh Mint-Coriander Chutney",
                    "1/4 cup Finely diced tomatoes & onions",
                    "1/2 tsp Chaat masala"
                ),
                instructions = listOf(
                    "Quarter boiled egg whites and arrange on plate.",
                    "Spoon spicy mint chutney over egg whites.",
                    "Top with diced tomatoes, onions, chaat masala, and lemon juice."
                )
            ),
            Recipe(
                id = "recipe_ind_13",
                title = "Roasted Sweet Potato (Shakarkandi) Chaat",
                description = "Carb-loading pre-workout Indian street snack made with air-roasted sweet potatoes and pomegranate.",
                category = "Indian",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 5,
                cookTimeMinutes = 10,
                calories = 210,
                proteinGrams = 4,
                carbsGrams = 44,
                fatGrams = 2,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "2 Medium Sweet Potatoes, cubed",
                    "2 tbsp Pomegranate arils",
                    "1/2 tsp Chaat masala & Rock salt",
                    "1 tbsp Fresh Lemon juice"
                ),
                instructions = listOf(
                    "Air-fry sweet potato cubes at 390°F for 10 minutes until crispy.",
                    "Toss with chaat masala, rock salt, and lemon juice.",
                    "Top with fresh pomegranate arils."
                )
            ),
            Recipe(
                id = "recipe_ind_14",
                title = "No-Bake Dates & Seed Protein Ladoo",
                description = "Guilt-free sweet energy bites made with soft Medjool dates, toasted oats, chia seeds, and protein powder.",
                category = "Indian",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 10,
                cookTimeMinutes = 0,
                calories = 190,
                proteinGrams = 10,
                carbsGrams = 26,
                fatGrams = 6,
                servings = 6,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1517673400267-0251440c45dc?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Pitted soft dates",
                    "1 cup Rolled oats, toasted",
                    "2 scoops Chocolate Whey protein powder",
                    "2 tbsp Chia seeds & Flaxseeds"
                ),
                instructions = listOf(
                    "Pulse pitted dates in food processor until sticky.",
                    "Add oats, protein powder, chia seeds, and pulse into dough.",
                    "Roll into 6 round ladoos and refrigerate."
                )
            ),
            Recipe(
                id = "recipe_ind_15",
                title = "Crispy Air-Fried Soya & Paneer Kebabs",
                description = "Savory high-protein vegetarian patties made with ground soya granules, low-fat paneer, and mint spices.",
                category = "Indian",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 5,
                calories = 280,
                proteinGrams = 28,
                carbsGrams = 14,
                fatGrams = 9,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Soya granules, soaked & squeezed",
                    "100g Low-fat Paneer, grated",
                    "1 tbsp Ginger-garlic-chili paste",
                    "1 tsp Garam masala & Chaat masala"
                ),
                instructions = listOf(
                    "Blend squeezed soya granules and grated paneer with spices.",
                    "Shape into patties and air fry at 380°F for 10 minutes.",
                    "Serve with mint chutney."
                )
            ),

            // ==========================================
            // --- 2. QUICK <15m RECIPES (10) ---
            // ==========================================
            Recipe(
                id = "recipe_q15_1",
                title = "5-Minute Berry Avocado Protein Smoothie",
                description = "Ultra-fast creamy protein smoothie packed with antioxidant-rich blueberries, avocado, and vanilla whey.",
                category = "Quick <15m",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                calories = 340,
                proteinGrams = 30,
                carbsGrams = 28,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 scoop Vanilla Whey Protein",
                    "1 cup Frozen Mixed Berries",
                    "1/2 Ripe Avocado",
                    "1 cup Unsweetened Almond Milk",
                    "1 tbsp Chia Seeds"
                ),
                instructions = listOf(
                    "Add almond milk, whey protein, frozen berries, avocado, and chia seeds to blender.",
                    "Blend on high speed for 45 seconds until silky smooth.",
                    "Pour into glass and enjoy immediately."
                )
            ),
            Recipe(
                id = "recipe_q15_2",
                title = "Express Smoked Salmon & Avocado Toast",
                description = "Crispy sourdough topped with mashed avocado, smoked salmon, capers, and a pinch of red pepper flakes.",
                category = "Quick <15m",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 5,
                cookTimeMinutes = 3,
                calories = 360,
                proteinGrams = 24,
                carbsGrams = 26,
                fatGrams = 16,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "2 slices Whole Grain Sourdough bread",
                    "80g Wild Smoked Salmon slices",
                    "1/2 Avocado, mashed",
                    "1 tsp Capers & Lemon juice",
                    "Red pepper flakes"
                ),
                instructions = listOf(
                    "Toast sourdough slices until crispy.",
                    "Spread mashed avocado over toasted bread.",
                    "Layer smoked salmon slices on top, sprinkle capers and red pepper flakes, and squeeze fresh lemon juice."
                )
            ),
            Recipe(
                id = "recipe_q15_3",
                title = "Quick Cottage Cheese & Berry Power Bowl",
                description = "No-cook high-casein bowl combining creamy low-fat cottage cheese, fresh berries, honey, and sliced almonds.",
                category = "Quick <15m",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                calories = 280,
                proteinGrams = 28,
                carbsGrams = 22,
                fatGrams = 6,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Low-fat Cottage Cheese",
                    "1/2 cup Fresh Strawberries & Blueberries",
                    "1 tbsp Sliced Almonds",
                    "1 tsp Honey or Maple syrup"
                ),
                instructions = listOf(
                    "Scoop cottage cheese into a bowl.",
                    "Top with fresh berries and sliced almonds.",
                    "Drizzle honey over top and serve instantly."
                )
            ),
            Recipe(
                id = "recipe_q15_4",
                title = "10-Minute Chili Lime Shrimp Skillet",
                description = "Succulent shrimp seared in chili, garlic, lime juice, and butter for a rapid low-carb protein fix.",
                category = "Quick <15m",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 260,
                proteinGrams = 32,
                carbsGrams = 4,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Raw Jumbo Shrimp, peeled & deveined",
                    "1 tbsp Grass-fed Butter or Olive oil",
                    "2 cloves Garlic, minced",
                    "1 tsp Chili powder & Lime juice",
                    "Fresh cilantro"
                ),
                instructions = listOf(
                    "Melt butter in hot skillet, add garlic and chili powder.",
                    "Add shrimp in a single layer and sear for 2 minutes per side until pink.",
                    "Squeeze lime juice over shrimp, sprinkle cilantro, and remove from heat."
                )
            ),
            Recipe(
                id = "recipe_q15_5",
                title = "Microwave Protein Chocolate Mug Cake",
                description = "Gourmet warm chocolate cake cooked in 90 seconds in a mug using whey protein, cocoa, and oat flour.",
                category = "Quick <15m",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 3,
                cookTimeMinutes = 2,
                calories = 220,
                proteinGrams = 25,
                carbsGrams = 18,
                fatGrams = 4,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1579372786545-d24232daf58c?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 scoop Chocolate Whey Protein",
                    "2 tbsp Oat Flour",
                    "1 tbsp Unsweetened Cocoa Powder",
                    "1/2 tsp Baking powder",
                    "1/4 cup Almond milk"
                ),
                instructions = listOf(
                    "Whisk dry ingredients in a microwave-safe mug.",
                    "Add almond milk and stir into smooth batter.",
                    "Microwave on high for 60-70 seconds until cake rises."
                )
            ),
            Recipe(
                id = "recipe_q15_6",
                title = "Speedy Greek Yogurt & Peanut Butter Parfait",
                description = "Layered high-protein snack with thick non-fat Greek yogurt, natural peanut butter, and dark chocolate chips.",
                category = "Quick <15m",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                calories = 310,
                proteinGrams = 26,
                carbsGrams = 24,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Non-fat Plain Greek Yogurt",
                    "2 tbsp Natural Peanut Butter",
                    "1 tbsp Dark Chocolate Chips",
                    "1/2 Sliced Banana"
                ),
                instructions = listOf(
                    "Layer Greek yogurt and peanut butter in a glass jar.",
                    "Top with sliced banana and dark chocolate chips."
                )
            ),
            Recipe(
                id = "recipe_q15_7",
                title = "12-Minute Seared Tuna Steak Salad",
                description = "Ahi tuna steak flash-seared and served over baby arugula with sesame soy dressing.",
                category = "Quick <15m",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 320,
                proteinGrams = 38,
                carbsGrams = 6,
                fatGrams = 14,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1501595091296-3aa970afb3ff?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "170g Ahi Tuna Steak",
                    "2 cups Baby Arugula",
                    "1 tbsp Sesame oil & Soy sauce",
                    "1 tsp Everything Bagel seasoning"
                ),
                instructions = listOf(
                    "Coat tuna steak with sesame seeds and soy sauce.",
                    "Sear in hot skillet for 90 seconds per side.",
                    "Slice thinly and place over arugula bed."
                )
            ),
            Recipe(
                id = "recipe_q15_8",
                title = "Quick Spicy Egg White Omelet Wrap",
                description = "Thin egg white crepe folded with spinach, turkey breast, and hot sauce in under 10 minutes.",
                category = "Quick <15m",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 3,
                cookTimeMinutes = 5,
                calories = 210,
                proteinGrams = 28,
                carbsGrams = 4,
                fatGrams = 5,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Egg Whites (approx 6 whites)",
                    "50g Sliced Turkey Breast",
                    "1/2 cup Baby Spinach",
                    "1 tbsp Sriracha or Hot sauce"
                ),
                instructions = listOf(
                    "Pour egg whites into hot non-stick pan.",
                    "Top with spinach and turkey slices.",
                    "Fold into wrap once set and drizzle hot sauce."
                )
            ),
            Recipe(
                id = "recipe_q15_9",
                title = "Fast Mediterranean Chickpea & Feta Salad",
                description = "No-cook fiber and protein salad with chickpeas, cucumbers, cherry tomatoes, olives, and feta.",
                category = "Quick <15m",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 8,
                cookTimeMinutes = 0,
                calories = 290,
                proteinGrams = 16,
                carbsGrams = 34,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 can Chickpeas, rinsed & drained",
                    "1/2 cup Cherry tomatoes & Cucumbers",
                    "30g Crumble Feta cheese",
                    "1 tbsp Extra virgin olive oil & Oregano"
                ),
                instructions = listOf(
                    "Toss chickpeas, tomatoes, cucumbers, and feta in bowl.",
                    "Drizzle olive oil and oregano seasoning."
                )
            ),
            Recipe(
                id = "recipe_q15_10",
                title = "10-Minute Garlic Butter Shrimp Zoodles",
                description = "Spiralized zucchini noodles cooked with garlic shrimp and parmesan cheese for a rapid low-carb dinner.",
                category = "Quick <15m",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 270,
                proteinGrams = 29,
                carbsGrams = 8,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Shrimp, peeled",
                    "2 Medium Zucchinis, spiralized",
                    "2 cloves Garlic, minced",
                    "1 tbsp Butter & Parmesan cheese"
                ),
                instructions = listOf(
                    "Sauté garlic and shrimp in butter for 3 minutes.",
                    "Toss zoodles in skillet for 2 minutes until tender.",
                    "Top with grated parmesan cheese."
                )
            ),

            // ==========================================
            // --- 3. HIGH PROTEIN RECIPES (10) ---
            // ==========================================
            Recipe(
                id = "recipe_hp_1",
                title = "Anabolic Grilled Chicken & Sweet Potato Mash",
                description = "Classic bodybuilder staple featuring 52g protein from herb-marinated grilled chicken breast served with sweet potato mash.",
                category = "High Protein",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 15,
                cookTimeMinutes = 20,
                calories = 490,
                proteinGrams = 52,
                carbsGrams = 42,
                fatGrams = 9,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "220g Skinless Chicken Breast",
                    "200g Sweet Potato, steamed & mashed",
                    "1 tsp Rosemary, Thyme & Garlic powder",
                    "1 tbsp Olive oil",
                    "1 cup Steamed Broccoli florets"
                ),
                instructions = listOf(
                    "Season chicken breast with garlic, rosemary, salt, and olive oil.",
                    "Grill or pan-sear for 6-8 minutes per side until internal temp reaches 165°F.",
                    "Serve alongside mashed sweet potatoes and steamed broccoli."
                )
            ),
            Recipe(
                id = "recipe_hp_2",
                title = "Triple-Meat Steak & Egg Breakfast Skillet",
                description = "Powerhouse breakfast skillet combining sirloin steak bites, egg whites, and whole eggs for 56g protein.",
                category = "High Protein",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 540,
                proteinGrams = 56,
                carbsGrams = 8,
                fatGrams = 28,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Lean Sirloin Steak, cubed",
                    "2 Whole Eggs + 3 Egg Whites",
                    "1/2 cup Diced bell peppers & onions",
                    "1 tsp Butter or Beef tallow"
                ),
                instructions = listOf(
                    "Sear steak bites in hot skillet with butter for 4 minutes.",
                    "Push steak to side, pour egg mixture and veggies into skillet.",
                    "Scramble until eggs are soft and cooked through."
                )
            ),
            Recipe(
                id = "recipe_hp_3",
                title = "Monster Whey & Oat Protein Pancake Stack",
                description = "Fluffy protein pancake stack packed with 42g protein, oats, whey, egg whites, and maple syrup.",
                category = "High Protein",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 460,
                proteinGrams = 42,
                carbsGrams = 48,
                fatGrams = 8,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 scoops Whey Protein powder",
                    "1/2 cup Rolled Oats ground into flour",
                    "1/2 cup Egg Whites",
                    "1/2 tsp Baking powder",
                    "2 tbsp Sugar-free Maple syrup"
                ),
                instructions = listOf(
                    "Blend oats, protein powder, egg whites, and baking powder into smooth batter.",
                    "Pour onto hot griddle and cook 2 minutes per side until golden.",
                    "Stack high and drizzle sugar-free maple syrup."
                )
            ),
            Recipe(
                id = "recipe_hp_4",
                title = "Pan-Seared Salmon Fillet with Asparagus",
                description = "Omega-3 rich wild salmon fillet pan-seared with crispy skin, served over roasted asparagus.",
                category = "High Protein",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 10,
                cookTimeMinutes = 12,
                calories = 480,
                proteinGrams = 44,
                carbsGrams = 6,
                fatGrams = 28,
                servings = 1,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Salmon Fillet skin-on",
                    "1 bunch Asparagus spears",
                    "1 tbsp Butter & Lemon juice",
                    "Garlic salt & Dill"
                ),
                instructions = listOf(
                    "Sear salmon skin-side down in butter for 5 minutes until crispy.",
                    "Flip and cook 3 minutes longer.",
                    "Sauté asparagus in same pan for 4 minutes."
                )
            ),
            Recipe(
                id = "recipe_hp_5",
                title = "Lean Beef Burger Bowl with Avocado & Brown Rice",
                description = "Deconstructed high-protein burger bowl with 93% lean ground beef, brown rice, avocado, and pickles.",
                category = "High Protein",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 12,
                calories = 520,
                proteinGrams = 48,
                carbsGrams = 38,
                fatGrams = 18,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g 93/7 Lean Ground Beef",
                    "1 cup Cooked Brown Rice",
                    "1/2 Avocado, sliced",
                    "2 tbsp Shredded Cheddar",
                    "Pickle slices & mustard"
                ),
                instructions = listOf(
                    "Brown ground beef in skillet with salt and pepper.",
                    "Assemble bowl with brown rice, beef, avocado, cheese, and pickles."
                )
            ),
            Recipe(
                id = "recipe_hp_6",
                title = "Grilled Turkey Breast Power Salad",
                description = "Lean sliced turkey breast served over a massive bed of spinach, cucumber, hard-boiled eggs, and walnuts.",
                category = "High Protein",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 420,
                proteinGrams = 45,
                carbsGrams = 12,
                fatGrams = 18,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Turkey Breast, sliced",
                    "2 cups Baby Spinach",
                    "2 Hard-boiled Eggs, sliced",
                    "2 tbsp Chopped Walnuts & Olive oil dressing"
                ),
                instructions = listOf(
                    "Grill turkey breast slices for 8 minutes.",
                    "Assemble spinach, sliced eggs, walnuts, and top with warm turkey breast."
                )
            ),
            Recipe(
                id = "recipe_hp_7",
                title = "High-Protein Greek Yogurt Collagen Bowl",
                description = "Double-protein bowl pairing non-fat Greek yogurt with collagen peptides, berries, and almond butter.",
                category = "High Protein",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                calories = 340,
                proteinGrams = 38,
                carbsGrams = 22,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 cups Non-fat Greek Yogurt",
                    "1 scoop Hydrolyzed Collagen Peptides",
                    "1/2 cup Strawberries",
                    "1 tbsp Almond butter"
                ),
                instructions = listOf(
                    "Whisk collagen peptides into Greek yogurt until smooth.",
                    "Top with fresh strawberries and almond butter."
                )
            ),
            Recipe(
                id = "recipe_hp_8",
                title = "Seared Ahi Tuna Bowl with Quinoa",
                description = "Pan-seared sesame ahi tuna served over protein quinoa, edamame, and cucumber slices.",
                category = "High Protein",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 8,
                calories = 460,
                proteinGrams = 46,
                carbsGrams = 36,
                fatGrams = 12,
                servings = 1,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1501595091296-3aa970afb3ff?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Ahi Tuna Steak",
                    "1 cup Cooked Quinoa",
                    "1/2 cup Steamed Edamame",
                    "1 tbsp Soy Sauce & Sesame oil"
                ),
                instructions = listOf(
                    "Sear ahi tuna for 90 seconds per side.",
                    "Assemble quinoa, edamame, and sliced tuna in bowl."
                )
            ),
            Recipe(
                id = "recipe_hp_9",
                title = "Herb-Crusted Roasted Cod Fillet with Broccoli",
                description = "Flaky white cod baked with a crispy herb parmesan crust served with garlic roasted broccoli.",
                category = "High Protein",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                calories = 360,
                proteinGrams = 42,
                carbsGrams = 10,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "220g Cod Fillet",
                    "2 tbsp Grated Parmesan & Breadcrumbs",
                    "1.5 cups Broccoli florets",
                    "1 tbsp Olive oil"
                ),
                instructions = listOf(
                    "Press parmesan herb mixture onto cod fillet.",
                    "Bake at 400°F for 15 minutes alongside broccoli."
                )
            ),
            Recipe(
                id = "recipe_hp_10",
                title = "Ultra-Lean Beef Muscle Chili Bowl",
                description = "Hearty protein chili loaded with lean ground beef, kidney beans, crushed tomatoes, and chili spices.",
                category = "High Protein",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 15,
                cookTimeMinutes = 25,
                calories = 510,
                proteinGrams = 50,
                carbsGrams = 40,
                fatGrams = 14,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "350g 95/5 Extra Lean Ground Beef",
                    "1 can Dark Red Kidney Beans",
                    "1.5 cups Crushed Tomatoes",
                    "1 tbsp Chili powder & Cumin"
                ),
                instructions = listOf(
                    "Brown ground beef in pot and drain fat.",
                    "Add crushed tomatoes, kidney beans, and spices.",
                    "Simmer on low for 20 minutes."
                )
            ),

            // ==========================================
            // --- 4. LOW CARB RECIPES (10) ---
            // ==========================================
            Recipe(
                id = "recipe_lc_1",
                title = "Keto Garlic Butter Steak Bites",
                description = "Juicy tender sirloin steak bites seared in garlic butter served with roasted cauliflower.",
                category = "Low Carb",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 440,
                proteinGrams = 42,
                carbsGrams = 6,
                fatGrams = 28,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Sirloin Steak, cubed",
                    "1.5 cups Cauliflower florets",
                    "2 tbsp Butter & 3 cloves Garlic",
                    "Rosemary & Salt"
                ),
                instructions = listOf(
                    "Sear steak bites in foaming garlic butter for 4 minutes.",
                    "Sauté cauliflower florets in remaining butter until tender."
                )
            ),
            Recipe(
                id = "recipe_lc_2",
                title = "Zucchini Noodle Turkey Bolognese",
                description = "Lean ground turkey simmered in marinara sauce over fresh spiralized zucchini noodles.",
                category = "Low Carb",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                calories = 330,
                proteinGrams = 36,
                carbsGrams = 10,
                fatGrams = 14,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1551183053-bf91a1d81141?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Lean Ground Turkey",
                    "2 Medium Zucchinis, spiralized",
                    "1/2 cup Low-carb Marinara sauce",
                    "1 tbsp Olive oil & Parmesan"
                ),
                instructions = listOf(
                    "Brown turkey in pan, add marinara sauce, and simmer.",
                    "Flash-sauté zoodles for 2 minutes and ladle bolognese over top."
                )
            ),
            Recipe(
                id = "recipe_lc_3",
                title = "Bacon Wrapped Grilled Chicken Tenders",
                description = "Lean chicken tenders wrapped in crispy bacon strips with smoky paprika seasoning.",
                category = "Low Carb",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                calories = 410,
                proteinGrams = 44,
                carbsGrams = 2,
                fatGrams = 24,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Chicken Tenders",
                    "4 strips Uncured Bacon",
                    "1 tsp Smoked Paprika & Garlic powder"
                ),
                instructions = listOf(
                    "Wrap bacon strips tightly around seasoned chicken tenders.",
                    "Air fry at 390°F for 14 minutes until bacon is super crispy."
                )
            ),
            Recipe(
                id = "recipe_lc_4",
                title = "Lemon Herb Pan-Fried Cod with Asparagus",
                description = "Ultra low-carb white fish cooked in lemon butter sauce alongside pan-roasted asparagus.",
                category = "Low Carb",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 310,
                proteinGrams = 38,
                carbsGrams = 5,
                fatGrams = 14,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Cod Fillet",
                    "1 bunch Asparagus",
                    "1 tbsp Butter & Lemon juice"
                ),
                instructions = listOf(
                    "Pan-fry cod in butter for 4 minutes per side.",
                    "Sauté asparagus in pan juices and squeeze fresh lemon."
                )
            ),
            Recipe(
                id = "recipe_lc_5",
                title = "Keto Creamy Spinach Stuffed Chicken",
                description = "Chicken breast stuffed with cream cheese, spinach, and mozzarella, baked to golden perfection.",
                category = "Low Carb",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 15,
                cookTimeMinutes = 25,
                calories = 480,
                proteinGrams = 48,
                carbsGrams = 4,
                fatGrams = 28,
                servings = 1,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "220g Chicken Breast",
                    "2 tbsp Cream Cheese & Mozzarella",
                    "1/2 cup Cooked Spinach"
                ),
                instructions = listOf(
                    "Cut pocket into chicken breast.",
                    "Stuff with spinach cheese mixture and bake at 380°F for 25 minutes."
                )
            ),
            Recipe(
                id = "recipe_lc_6",
                title = "Low-Carb Bacon Cheeseburger Bowl",
                description = "All the flavors of a bacon cheeseburger served over shredded lettuce with special sauce.",
                category = "Low Carb",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 450,
                proteinGrams = 40,
                carbsGrams = 8,
                fatGrams = 28,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Lean Ground Beef",
                    "2 strips Bacon",
                    "2 cups Shredded Lettuce",
                    "1 slice Cheddar & Mayo dressing"
                ),
                instructions = listOf(
                    "Brown beef and crispy bacon in pan.",
                    "Assemble over lettuce bed with cheddar cheese."
                )
            ),
            Recipe(
                id = "recipe_lc_7",
                title = "Avocado Stuffed with Spicy Tuna Salad",
                description = "Ripe avocado halves filled with wild albacore tuna, mayo, sriracha, and diced celery.",
                category = "Low Carb",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 0,
                calories = 380,
                proteinGrams = 32,
                carbsGrams = 7,
                fatGrams = 24,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 Large Avocado, halved & pitted",
                    "1 can Albacore Tuna",
                    "1 tbsp Mayo & Sriracha",
                    "Diced celery"
                ),
                instructions = listOf(
                    "Mix canned tuna with mayo, sriracha, and celery.",
                    "Scoop tuna salad into avocado hollows."
                )
            ),
            Recipe(
                id = "recipe_lc_8",
                title = "Cauliflower Rice Chicken Fried Rice",
                description = "Keto takeout alternative featuring grated cauliflower rice cooked with chicken breast and sesame oil.",
                category = "Low Carb",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 310,
                proteinGrams = 38,
                carbsGrams = 9,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Chicken Breast, diced",
                    "2 cups Riced Cauliflower",
                    "1 Egg, beaten",
                    "1 tbsp Soy Sauce & Sesame oil"
                ),
                instructions = listOf(
                    "Sauté chicken cubes in sesame oil.",
                    "Add cauliflower rice, soy sauce, and scramble egg in center."
                )
            ),
            Recipe(
                id = "recipe_lc_9",
                title = "Grilled Salmon & Pesto Asparagus Skillet",
                description = "Wild salmon seared in basil pesto served alongside roasted garlic asparagus spears.",
                category = "Low Carb",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 12,
                calories = 460,
                proteinGrams = 40,
                carbsGrams = 5,
                fatGrams = 30,
                servings = 1,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Salmon Fillet",
                    "2 tbsp Basil Pesto",
                    "1 bunch Asparagus spears"
                ),
                instructions = listOf(
                    "Coat salmon in basil pesto.",
                    "Sear in skillet alongside asparagus for 10 minutes total."
                )
            ),
            Recipe(
                id = "recipe_lc_10",
                title = "Scrambled Eggs with Smoked Salmon & Spinach",
                description = "Fluffy pasture-raised scrambled eggs with smoked salmon ribbons and wilted spinach.",
                category = "Low Carb",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 320,
                proteinGrams = 30,
                carbsGrams = 3,
                fatGrams = 20,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "3 Large Eggs",
                    "60g Smoked Salmon",
                    "1 cup Baby Spinach",
                    "1 tsp Butter"
                ),
                instructions = listOf(
                    "Wilt spinach in buttered pan.",
                    "Pour beaten eggs and fold in smoked salmon ribbons before serving."
                )
            ),

            // ==========================================
            // --- 5. PRE-WORKOUT RECIPES (6) ---
            // ==========================================
            Recipe(
                id = "recipe_pre_1",
                title = "Pre-Workout Honey & Banana Oat Porridge",
                description = "Fast-digesting complex carbs and natural sugars designed for sustained intra-workout stamina.",
                category = "Pre-Workout",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 340,
                proteinGrams = 16,
                carbsGrams = 54,
                fatGrams = 6,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1517673400267-0251440c45dc?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1/2 cup Quick Rolled Oats",
                    "1 Ripe Banana, sliced",
                    "1 tbsp Raw Honey",
                    "1 scoop Whey Protein",
                    "Cinnamon powder"
                ),
                instructions = listOf(
                    "Cook oats with water in microwave for 2 minutes.",
                    "Stir in whey protein, top with banana slices, and drizzle honey."
                )
            ),
            Recipe(
                id = "recipe_pre_2",
                title = "Rice Cakes with Almond Butter & Banana",
                description = "Rapid glycogen loading snack made with crunchy brown rice cakes, almond butter, and honey.",
                category = "Pre-Workout",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 3,
                cookTimeMinutes = 0,
                calories = 290,
                proteinGrams = 10,
                carbsGrams = 42,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1517673400267-0251440c45dc?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "3 Brown Rice Cakes",
                    "2 tbsp Almond Butter",
                    "1/2 Banana, sliced",
                    "1 tbsp Honey & Chia seeds"
                ),
                instructions = listOf(
                    "Spread almond butter across rice cakes.",
                    "Top with sliced banana and honey drizzle."
                )
            ),
            Recipe(
                id = "recipe_pre_3",
                title = "Beetroot & Berry Nitric Oxide Pre-Workout Shake",
                description = "Natural nitric oxide booster loaded with dietary nitrates from beetroot powder and berries for intense muscle pumps.",
                category = "Pre-Workout",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                calories = 210,
                proteinGrams = 8,
                carbsGrams = 38,
                fatGrams = 2,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 tbsp Organic Beetroot Powder",
                    "1 cup Frozen Berries",
                    "1 cup Coconut Water",
                    "1 tbsp Honey"
                ),
                instructions = listOf(
                    "Blend coconut water, beetroot powder, berries, and honey.",
                    "Drink 30 minutes before heavy lifting."
                )
            ),
            Recipe(
                id = "recipe_pre_4",
                title = "Sweet Potato & Cinnamon Glycogen Bowl",
                description = "Steamed sweet potatoes mashed with cinnamon and maple syrup for long-lasting workout energy.",
                category = "Pre-Workout",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 5,
                cookTimeMinutes = 10,
                calories = 240,
                proteinGrams = 6,
                carbsGrams = 48,
                fatGrams = 3,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Sweet Potato, steamed",
                    "1 tsp Cinnamon",
                    "1 tbsp Maple syrup",
                    "Pinch of sea salt"
                ),
                instructions = listOf(
                    "Mash warm sweet potato with fork.",
                    "Stir in cinnamon, maple syrup, and sea salt."
                )
            ),
            Recipe(
                id = "recipe_pre_5",
                title = "Apple & Peanut Butter Toast",
                description = "Simple toast topped with natural peanut butter and thinly sliced crisp apples.",
                category = "Pre-Workout",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 5,
                cookTimeMinutes = 2,
                calories = 270,
                proteinGrams = 9,
                carbsGrams = 36,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 slice Sourdough Toast",
                    "1.5 tbsp Peanut butter",
                    "1/2 Honeycrisp Apple, sliced"
                ),
                instructions = listOf(
                    "Spread peanut butter over warm sourdough toast.",
                    "Arrange apple slices on top."
                )
            ),
            Recipe(
                id = "recipe_pre_6",
                title = "Dates, Chia & Espresso Energy Chews",
                description = "Caffeine infused date and espresso energy bites for instant focus and stamina.",
                category = "Pre-Workout",
                fitnessGoal = "Energy Booster",
                prepTimeMinutes = 10,
                cookTimeMinutes = 0,
                calories = 200,
                proteinGrams = 6,
                carbsGrams = 40,
                fatGrams = 4,
                servings = 4,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1517673400267-0251440c45dc?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Dates",
                    "1 shot Espresso (cooled)",
                    "2 tbsp Chia seeds",
                    "1/4 cup Oats"
                ),
                instructions = listOf(
                    "Blend dates, espresso, oats, and chia seeds in food processor.",
                    "Roll into 4 bite-sized chews."
                )
            ),

            // ==========================================
            // --- 6. POST-WORKOUT RECIPES (7) ---
            // ==========================================
            Recipe(
                id = "recipe_post_1",
                title = "Anabolic Post-Workout Whey & Rice Krispie Shake",
                description = "Rapid muscle glycogen replenishment drink pairing fast-acting whey isolate with crisp rice cereal.",
                category = "Post-Workout",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 3,
                cookTimeMinutes = 0,
                calories = 410,
                proteinGrams = 45,
                carbsGrams = 50,
                fatGrams = 3,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "2 scoops Whey Protein Isolate",
                    "1 cup Rice Krispies Cereal",
                    "1 cup Skim Milk or Water",
                    "1 tbsp Honey"
                ),
                instructions = listOf(
                    "Shake whey isolate in cold skim milk.",
                    "Pour over bowl of rice cereal and honey."
                )
            ),
            Recipe(
                id = "recipe_post_2",
                title = "Chicken, Quinoa & Veggie Glycogen Bowl",
                description = "Balanced recovery bowl containing lean grilled chicken breast, fluffy quinoa, and roasted sweet corn.",
                category = "Post-Workout",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                calories = 510,
                proteinGrams = 48,
                carbsGrams = 52,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Chicken Breast",
                    "1.5 cups Cooked Quinoa",
                    "1/2 cup Sweet corn & roasted peppers"
                ),
                instructions = listOf(
                    "Grill chicken breast for 8 minutes.",
                    "Combine with warm quinoa and roasted corn."
                )
            ),
            Recipe(
                id = "recipe_post_3",
                title = "Post-Workout Salmon & Jasmine Rice Plate",
                description = "Seared salmon fillet served over fast-digesting fragrant jasmine rice.",
                category = "Post-Workout",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 10,
                cookTimeMinutes = 12,
                calories = 520,
                proteinGrams = 42,
                carbsGrams = 45,
                fatGrams = 18,
                servings = 1,
                difficulty = "Medium",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Salmon Fillet",
                    "1.5 cups Cooked Jasmine Rice",
                    "1 tbsp Soy sauce & Teriyaki"
                ),
                instructions = listOf(
                    "Pan-sear salmon fillet for 10 minutes.",
                    "Serve over steaming jasmine rice with teriyaki sauce."
                )
            ),
            Recipe(
                id = "recipe_post_4",
                title = "Lean Beef, Sweet Potato & Broccoli Bowl",
                description = "Heavy lifting recovery plate combining extra lean ground beef, roasted sweet potatoes, and broccoli.",
                category = "Post-Workout",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                calories = 540,
                proteinGrams = 50,
                carbsGrams = 48,
                fatGrams = 14,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g 93/7 Lean Ground Beef",
                    "200g Roasted Sweet Potato cubes",
                    "1 cup Broccoli florets"
                ),
                instructions = listOf(
                    "Brown ground beef in pan.",
                    "Assemble bowl with roasted sweet potatoes and steamed broccoli."
                )
            ),
            Recipe(
                id = "recipe_post_5",
                title = "Egg White & Cottage Cheese Protein Toast",
                description = "Fluffy scrambled egg whites and cottage cheese piled high on whole grain sourdough toast.",
                category = "Post-Workout",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 360,
                proteinGrams = 38,
                carbsGrams = 35,
                fatGrams = 6,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Egg Whites",
                    "1/2 cup Low-fat Cottage Cheese",
                    "2 slices Sourdough Toast"
                ),
                instructions = listOf(
                    "Scramble egg whites and cottage cheese together.",
                    "Mound over toasted sourdough bread."
                )
            ),
            Recipe(
                id = "recipe_post_6",
                title = "Chocolate Protein Rice Pudding with Berries",
                description = "Creamy post-workout rice pudding made with jasmine rice, chocolate whey, and raspberries.",
                category = "Post-Workout",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 5,
                cookTimeMinutes = 5,
                calories = 380,
                proteinGrams = 32,
                carbsGrams = 48,
                fatGrams = 4,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1579372786545-d24232daf58c?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 cups Warm Cooked Jasmine Rice",
                    "1 scoop Chocolate Whey Protein",
                    "1/2 cup Almond milk",
                    "Fresh Raspberries"
                ),
                instructions = listOf(
                    "Stir chocolate whey into warm jasmine rice and almond milk.",
                    "Top with fresh raspberries."
                )
            ),
            Recipe(
                id = "recipe_post_7",
                title = "Turkey Breast & Roasted Potatoes Recovery Plate",
                description = "Sliced turkey breast served alongside golden roasted baby potatoes and green beans.",
                category = "Post-Workout",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 10,
                cookTimeMinutes = 20,
                calories = 460,
                proteinGrams = 44,
                carbsGrams = 42,
                fatGrams = 10,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Turkey Breast",
                    "200g Baby Potatoes, roasted",
                    "1 cup Green beans"
                ),
                instructions = listOf(
                    "Roast baby potatoes at 400°F for 20 minutes.",
                    "Serve with sliced turkey breast and steamed green beans."
                )
            ),

            // ==========================================
            // --- 7. VEGAN RECIPES (7) ---
            // ==========================================
            Recipe(
                id = "recipe_vgn_1",
                title = "High-Protein Tofu & Edamame Buddha Bowl",
                description = "Nutrient-dense vegan bowl with crispy baked tofu, shelled edamame, quinoa, and peanut dressing.",
                category = "Vegan",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 15,
                cookTimeMinutes = 20,
                calories = 420,
                proteinGrams = 32,
                carbsGrams = 38,
                fatGrams = 16,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1546069901-d5d82eef4431?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "180g Extra Firm Tofu, cubed",
                    "1/2 cup Shelled Edamame",
                    "1 cup Cooked Quinoa",
                    "2 tbsp Peanut butter lime dressing"
                ),
                instructions = listOf(
                    "Bake tofu cubes at 400°F for 20 minutes until crispy.",
                    "Assemble quinoa, edamame, and baked tofu in bowl with peanut dressing."
                )
            ),
            Recipe(
                id = "recipe_vgn_2",
                title = "Creamy Peanut Butter Lentil & Chickpea Curry",
                description = "Rich plant-based protein curry made with red lentils, chickpeas, peanut butter, and spinach.",
                category = "Vegan",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 20,
                calories = 450,
                proteinGrams = 28,
                carbsGrams = 52,
                fatGrams = 14,
                servings = 2,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 cup Red Lentils & 1 can Chickpeas",
                    "2 tbsp Natural Peanut Butter",
                    "1.5 cups Coconut milk & tomato passata",
                    "2 cups Spinach"
                ),
                instructions = listOf(
                    "Simmer red lentils and chickpeas in tomato passata and coconut milk.",
                    "Stir in peanut butter and spinach until wilted."
                )
            ),
            Recipe(
                id = "recipe_vgn_3",
                title = "Tempeh Bacon & Avocado Quinoa Wrap",
                description = "Smoky maple-glazed tempeh bacon strips rolled into a tortilla with mashed avocado and spinach.",
                category = "Vegan",
                fitnessGoal = "Lean Cut",
                prepTimeMinutes = 10,
                cookTimeMinutes = 10,
                calories = 390,
                proteinGrams = 26,
                carbsGrams = 36,
                fatGrams = 16,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "120g Organic Tempeh, sliced",
                    "1 Whole Wheat Tortilla",
                    "1/2 Avocado, mashed",
                    "1 tbsp Soy sauce & Maple syrup"
                ),
                instructions = listOf(
                    "Pan-sear tempeh strips in soy sauce and maple syrup until crispy.",
                    "Assemble in tortilla with mashed avocado."
                )
            ),
            Recipe(
                id = "recipe_vgn_4",
                title = "Vegan Pea Protein Power Smoothie Bowl",
                description = "Thick green smoothie bowl powered by organic pea protein, spirulina, banana, and chia seeds.",
                category = "Vegan",
                fitnessGoal = "Recovery",
                prepTimeMinutes = 5,
                cookTimeMinutes = 0,
                calories = 360,
                proteinGrams = 30,
                carbsGrams = 42,
                fatGrams = 8,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 scoops Organic Plant Pea Protein",
                    "1 Frozen Banana",
                    "1 cup Spinach & Almond milk",
                    "Chia seeds & Granola"
                ),
                instructions = listOf(
                    "Blend frozen banana, spinach, pea protein, and almond milk into thick smoothie.",
                    "Pour into bowl and top with chia seeds and granola."
                )
            ),
            Recipe(
                id = "recipe_vgn_5",
                title = "Spicy Black Bean & Hemp Seed Fitness Tacos",
                description = "Warm corn tortillas filled with seasoned black beans, hemp seeds, guacamole, and salsa.",
                category = "Vegan",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 5,
                calories = 350,
                proteinGrams = 24,
                carbsGrams = 44,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1 can Black Beans, warm",
                    "2 tbsp Shelled Hemp Seeds",
                    "3 Corn Tortillas",
                    "Guacamole & Salsa"
                ),
                instructions = listOf(
                    "Warm corn tortillas on tawa.",
                    "Fill with seasoned black beans, guacamole, salsa, and sprinkle hemp seeds."
                )
            ),
            Recipe(
                id = "recipe_vgn_6",
                title = "Crispy Air-Fried Sesame Tofu & Broccoli",
                description = "Cube-cut tofu tossed in tamari and sesame oil, air-fried with broccoli florets over brown rice.",
                category = "Vegan",
                fitnessGoal = "Muscle Growth",
                prepTimeMinutes = 10,
                cookTimeMinutes = 15,
                calories = 380,
                proteinGrams = 28,
                carbsGrams = 34,
                fatGrams = 14,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1546069901-d5d82eef4431?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "200g Firm Tofu, cubed",
                    "1.5 cups Broccoli florets",
                    "1 tbsp Tamari & Sesame oil",
                    "1 cup Brown Rice"
                ),
                instructions = listOf(
                    "Coat tofu in tamari and sesame oil.",
                    "Air-fry at 390°F for 15 minutes alongside broccoli and serve over brown rice."
                )
            ),
            Recipe(
                id = "recipe_vgn_7",
                title = "Roasted Chickpea & Tahini Mediterranean Bowl",
                description = "Crispy oven-roasted spiced chickpeas served over brown rice with creamy lemon tahini sauce.",
                category = "Vegan",
                fitnessGoal = "Fat Loss",
                prepTimeMinutes = 10,
                cookTimeMinutes = 20,
                calories = 370,
                proteinGrams = 22,
                carbsGrams = 48,
                fatGrams = 12,
                servings = 1,
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80",
                ingredients = listOf(
                    "1.5 cups Roasted Chickpeas",
                    "1 cup Cooked Brown Rice",
                    "2 tbsp Creamy Tahini lemon dressing",
                    "Sliced cucumbers & tomatoes"
                ),
                instructions = listOf(
                    "Roast chickpeas with paprika and cumin at 400°F for 20 minutes.",
                    "Assemble over brown rice, veggies, and drizzle tahini dressing."
                )
            )
        )
    }
}
