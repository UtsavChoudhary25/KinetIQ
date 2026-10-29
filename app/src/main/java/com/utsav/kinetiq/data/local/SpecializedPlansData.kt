package com.utsav.kinetiq.data.local

import com.utsav.kinetiq.domain.model.Exercise
import com.utsav.kinetiq.domain.model.Workout
import com.utsav.kinetiq.domain.model.WorkoutExercise
import com.utsav.kinetiq.domain.model.WorkoutSet

enum class SpecializedDiscipline(val id: String, val title: String, val iconEmoji: String, val tagline: String) {
    ATHLETE("athlete", "Pro Athlete", "⚡", "Explosive Power, Speed & Agility"),
    BODYBUILDING("bodybuilding", "Bodybuilding", "🏋️‍♂️", "Maximum Hypertrophy & Muscle Volume"),
    POWERLIFTING("powerlifting", "Powerlifting", "🩸", "Heavy Max Strength Singles & Triples"),
    LEAN_SHRED("shred", "Lean Shred", "🔥", "Maximum Fat Burning & High Metabolism"),
    CALISTHENICS("calisthenics", "Calisthenics", "🤸", "Bodyweight Gymnastic Skill & Core Mastery"),
    YOGA("yoga", "Yoga & Recovery", "🧘", "Joint Longevity, Flexibility & Vinyasa")
}

data class SpecializedMeal(
    val mealType: String,
    val title: String,
    val description: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val ingredients: List<String>
)

data class SpecializedPlan(
    val discipline: SpecializedDiscipline,
    val targetCalories: Int,
    val proteinPercent: Int,
    val carbsPercent: Int,
    val fatPercent: Int,
    val focusDescription: String,
    val workout: Workout,
    val meals: List<SpecializedMeal>
)

object SpecializedPlansData {

    val plans: List<SpecializedPlan> = listOf(
        // ⚡ PRO ATHLETE
        SpecializedPlan(
            discipline = SpecializedDiscipline.ATHLETE,
            targetCalories = 3000,
            proteinPercent = 30,
            carbsPercent = 45,
            fatPercent = 25,
            focusDescription = "Optimized for fast-twitch muscle activation, vertical jump, rapid directional acceleration, and high endurance performance.",
            workout = Workout(
                id = "plan_athlete_workout",
                title = "Pro Athlete Explosive Power Circuit",
                durationMinutes = 50,
                difficulty = "Advanced",
                aiRationale = "Combines Olympic lifts, plyometrics, and sprinting intervals to maximize rate of force development (RFD).",
                exercises = listOf(
                    WorkoutExercise(
                        exercise = Exercise("ath_1", "Hang Power Clean", "Full Body", "Barbell", "Explosively drive hips forward, shrug shoulders, and catch bar on front deltoids."),
                        sets = listOf(WorkoutSet(5, 60f), WorkoutSet(5, 70f), WorkoutSet(3, 80f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("ath_2", "Plyometric Box Jump", "Legs", "Box", "Explode vertically onto box landing softly in a quarter squat position."),
                        sets = listOf(WorkoutSet(8, 0f), WorkoutSet(8, 0f), WorkoutSet(8, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("ath_3", "Medicine Ball Rotational Slam", "Core", "Medicine Ball", "Rotate torso powerfully and slam ball into ground next to feet."),
                        sets = listOf(WorkoutSet(12, 10f), WorkoutSet(12, 10f), WorkoutSet(12, 10f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("ath_4", "Single-Leg Dumbbell Step-Up", "Legs", "Dumbbells", "Drive through lead heel without pushing off trailing leg."),
                        sets = listOf(WorkoutSet(10, 20f), WorkoutSet(10, 20f), WorkoutSet(10, 20f))
                    )
                )
            ),
            meals = listOf(
                SpecializedMeal("Breakfast", "Athletic Power Oats with Whey & Berries", "High glycogen replenishment with clean fast-acting whey protein.", 750, 42, 95, 14, listOf("1 cup Oats", "1.5 scoops Whey Protein", "1/2 cup Blueberry", "1 tbsp Peanut Butter")),
                SpecializedMeal("Lunch", "Grilled Chicken Quinoa Bowl with Sweet Potato", "Balanced complex carbs and lean protein for sustained endurance.", 850, 52, 90, 18, listOf("200g Chicken Breast", "1 cup Quinoa", "1 Baked Sweet Potato", "Steamed Broccoli")),
                SpecializedMeal("Dinner", "Pan-Seared Salmon with Brown Rice & Asparagus", "Omega-3 fatty acids for anti-inflammatory muscle recovery.", 800, 48, 65, 26, listOf("200g Wild Salmon", "1 cup Brown Rice", "Grilled Asparagus", "1 tbsp Olive Oil")),
                SpecializedMeal("Snack", "Greek Yogurt Parfait with Honey & Walnuts", "Slow-digesting casein protein before rest.", 600, 32, 55, 16, listOf("1.5 cups Greek Yogurt", "2 tbsp Raw Honey", "30g Walnuts", "Sliced Banana"))
            )
        ),

        // 🏋️‍♂️ BODYBUILDING
        SpecializedPlan(
            discipline = SpecializedDiscipline.BODYBUILDING,
            targetCalories = 2800,
            proteinPercent = 40,
            carbsPercent = 40,
            fatPercent = 20,
            focusDescription = "Designed for maximal muscle hypertrophy, time-under-tension, high volume, and balanced aesthetic symmetry.",
            workout = Workout(
                id = "plan_bodybuilding_workout",
                title = "Hypertrophy Upper Body Pump",
                durationMinutes = 60,
                difficulty = "Intermediate",
                aiRationale = "High mechanical tension combined with metabolic stress to trigger muscle protein synthesis.",
                exercises = listOf(
                    WorkoutExercise(
                        exercise = Exercise("bb_1", "Incline Dumbbell Bench Press", "Chest", "Dumbbells", "Maintain 30-degree incline, stretch chest at bottom and contract at top."),
                        sets = listOf(WorkoutSet(12, 30f), WorkoutSet(10, 34f), WorkoutSet(8, 38f), WorkoutSet(8, 38f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("bb_2", "Bent-Over Barbell Row", "Back", "Barbell", "Pull bar towards lower abdomen, squeezing lats and rhomboids at peak."),
                        sets = listOf(WorkoutSet(10, 60f), WorkoutSet(10, 70f), WorkoutSet(8, 80f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("bb_3", "Cable Standing Flyes", "Chest", "Cables", "Focus on squeeze at midline with 2-second isometric peak contraction."),
                        sets = listOf(WorkoutSet(15, 15f), WorkoutSet(15, 15f), WorkoutSet(12, 18f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("bb_4", "EZ-Bar Preacher Curl", "Arms", "EZ-Bar", "Isolate biceps with arms rested on preacher pad, full stretch at bottom."),
                        sets = listOf(WorkoutSet(12, 25f), WorkoutSet(10, 30f), WorkoutSet(10, 30f))
                    )
                )
            ),
            meals = listOf(
                SpecializedMeal("Breakfast", "Bodybuilder Egg White & Whole Egg Scramble", "High-protein low-fat morning fuel.", 650, 48, 50, 16, listOf("6 Egg Whites + 2 Whole Eggs", "2 slices Whole Wheat Toast", "1/2 Avocado")),
                SpecializedMeal("Lunch", "Lean Beef & White Rice Meal Prep", "Fast-digesting muscle-building meal.", 800, 55, 80, 18, listOf("220g Lean Ground Beef (93/7)", "1.5 cups Jasmine Rice", "Grilled Zucchini")),
                SpecializedMeal("Dinner", "Turkey Breast with Baked Potato & Green Beans", "Clean lean protein source for overnight recovery.", 750, 52, 70, 12, listOf("200g Turkey Breast", "1 Large Russet Potato", "Steamed Green Beans")),
                SpecializedMeal("Snack", "Low-Fat Cottage Cheese with Almonds & Berries", "Slow-release casein protein night snack.", 600, 42, 45, 14, listOf("1.5 cups Cottage Cheese", "20g Sliced Almonds", "1/2 cup Strawberries"))
            )
        ),

        // 🩸 POWERLIFTING
        SpecializedPlan(
            discipline = SpecializedDiscipline.POWERLIFTING,
            targetCalories = 3200,
            proteinPercent = 35,
            carbsPercent = 45,
            fatPercent = 20,
            focusDescription = "Built around maximal force output for the Squat, Bench Press, and Deadlift. Focuses on heavy loads and CNS recovery.",
            workout = Workout(
                id = "plan_powerlifting_workout",
                title = "Heavy Powerlifting Strength Main Lifts",
                durationMinutes = 65,
                difficulty = "Advanced",
                aiRationale = "Low rep heavy load training (85-90% 1RM) targeting maximal motor unit recruitment.",
                exercises = listOf(
                    WorkoutExercise(
                        exercise = Exercise("pl_1", "Heavy Barbell Back Squat", "Legs", "Barbell", "Keep chest up, break parallel, and drive out of the hole with leg drive."),
                        sets = listOf(WorkoutSet(5, 100f), WorkoutSet(3, 120f), WorkoutSet(3, 130f), WorkoutSet(1, 140f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("pl_2", "Pause Bench Press", "Chest", "Barbell", "Lower to chest, pause for 1 second motionless, then press up explosively."),
                        sets = listOf(WorkoutSet(5, 80f), WorkoutSet(3, 95f), WorkoutSet(3, 100f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("pl_3", "Conventional Barbell Deadlift", "Back", "Barbell", "Hinge at hips, pull slack out of bar, and lock out hips firmly."),
                        sets = listOf(WorkoutSet(5, 120f), WorkoutSet(3, 140f), WorkoutSet(2, 160f))
                    )
                )
            ),
            meals = listOf(
                SpecializedMeal("Breakfast", "Powerlifter Peanut Butter Banana Oatmeal", "Calorie-dense fuel for maximal force generation.", 850, 40, 105, 22, listOf("1.5 cups Oats", "2 tbsp Peanut Butter", "2 Whole Bananas", "1 scoop Protein")),
                SpecializedMeal("Lunch", "Sirloin Steak with Roasted Red Potatoes", "Creatine-rich red meat for maximal strength output.", 950, 60, 85, 26, listOf("250g Sirloin Steak", "300g Roasted Red Potatoes", "Garlic Butter Spinach")),
                SpecializedMeal("Dinner", "Chicken Pasta Bolognese", "Glycogen restocking for CNS recovery.", 850, 52, 95, 18, listOf("200g Chicken Breast", "2 cups Whole Wheat Pasta", "Marinara Sauce")),
                SpecializedMeal("Snack", "Greek Yogurt Bowl with Granola & Honey", "Sustained night energy.", 550, 35, 65, 12, listOf("1.5 cups Greek Yogurt", "1/2 cup Honey Granola", "1 tbsp Honey"))
            )
        ),

        // 🔥 LEAN SHRED
        SpecializedPlan(
            discipline = SpecializedDiscipline.LEAN_SHRED,
            targetCalories = 1900,
            proteinPercent = 45,
            carbsPercent = 25,
            fatPercent = 30,
            focusDescription = "Designed to maximize caloric deficit while preserving lean muscle mass through high-density conditioning circuits.",
            workout = Workout(
                id = "plan_shred_workout",
                title = "High-Density Fat Burn Conditioning",
                durationMinutes = 40,
                difficulty = "Intermediate",
                aiRationale = "High heart rate metabolic conditioning combined with short rest intervals to boost EPOC (excess post-exercise oxygen consumption).",
                exercises = listOf(
                    WorkoutExercise(
                        exercise = Exercise("sh_1", "Kettlebell Swing", "Full Body", "Kettlebell", "Hinge at hips and snap glutes to drive kettlebell to eye level."),
                        sets = listOf(WorkoutSet(20, 20f), WorkoutSet(20, 20f), WorkoutSet(20, 20f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("sh_2", "Dumbbell Thruster", "Full Body", "Dumbbells", "Squat down then press dumbbells overhead in one continuous motion."),
                        sets = listOf(WorkoutSet(15, 12f), WorkoutSet(15, 12f), WorkoutSet(15, 12f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("sh_3", "Burpee Box Jump", "Full Body", "Box", "Drop into pushup, hop up, and jump onto box in quick rhythm."),
                        sets = listOf(WorkoutSet(12, 0f), WorkoutSet(12, 0f), WorkoutSet(12, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("sh_4", "Mountain Climbers", "Core", "Bodyweight", "Drive knees rapidly to chest while maintaining plank position."),
                        sets = listOf(WorkoutSet(30, 0f), WorkoutSet(30, 0f), WorkoutSet(30, 0f))
                    )
                )
            ),
            meals = listOf(
                SpecializedMeal("Breakfast", "Shredded Egg White & Spinach Omelet", "Ultra-low calorie high protein breakfast.", 400, 38, 12, 10, listOf("6 Egg Whites + 1 Whole Egg", "1 cup Spinach", "1/4 Avocado")),
                SpecializedMeal("Lunch", "Grilled Chicken Breast Caesar Salad (No Croutons)", "High volume fiber with lean protein.", 550, 48, 18, 16, listOf("200g Grilled Chicken", "3 cups Romaine Lettuce", "Light Caesar Dressing", "1 tbsp Parmesan")),
                SpecializedMeal("Dinner", "Baked Cod Fillet with Steamed Asparagus & Cauliflower Rice", "Zero-fat white fish for rapid deficit.", 500, 45, 20, 8, listOf("220g Cod Fillet", "1.5 cups Cauliflower Rice", "1 cup Steamed Asparagus")),
                SpecializedMeal("Snack", "Isolate Protein Shake with Almond Milk", "Pure protein boost without added carbs.", 450, 35, 10, 6, listOf("1.5 scoops Whey Isolate", "1 cup Unsweetened Almond Milk", "1/2 cup Ice"))
            )
        ),

        // 🤸 CALISTHENICS
        SpecializedPlan(
            discipline = SpecializedDiscipline.CALISTHENICS,
            targetCalories = 2300,
            proteinPercent = 35,
            carbsPercent = 40,
            fatPercent = 25,
            focusDescription = "Master relative bodyweight strength, core compression, straight-arm balance, and gymnastic skill control.",
            workout = Workout(
                id = "plan_calisthenics_workout",
                title = "Bodyweight Gymnastic Skill Routine",
                durationMinutes = 45,
                difficulty = "Intermediate",
                aiRationale = "Focuses on scapular depression, core tension, and tendon strength needed for bodyweight skills.",
                exercises = listOf(
                    WorkoutExercise(
                        exercise = Exercise("cal_1", "Pull-up to Muscle-up Progression", "Back", "Pull-up Bar", "Pull chest to bar and transition shoulders over bar smoothly."),
                        sets = listOf(WorkoutSet(8, 0f), WorkoutSet(8, 0f), WorkoutSet(6, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("cal_2", "Gymnastic Ring Dips", "Chest", "Rings", "Turn rings outward at top lockout position, descend to 90 degrees."),
                        sets = listOf(WorkoutSet(10, 0f), WorkoutSet(10, 0f), WorkoutSet(10, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("cal_3", "Archer Push-ups", "Chest", "Bodyweight", "Extend one arm straight while lowering body over opposite arm."),
                        sets = listOf(WorkoutSet(10, 0f), WorkoutSet(10, 0f), WorkoutSet(10, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("cal_4", "L-Sit Hold on Parallettes", "Core", "Parallettes", "Keep arms straight, depress shoulders, and lift legs parallel to ground."),
                        sets = listOf(WorkoutSet(20, 0f), WorkoutSet(20, 0f), WorkoutSet(20, 0f))
                    )
                )
            ),
            meals = listOf(
                SpecializedMeal("Breakfast", "Calisthenics Power Smoothie Bowl", "Light clean meal before bodyweight training.", 550, 32, 65, 12, listOf("1 scoop Whey Protein", "1 Frozen Banana", "1/2 cup Oats", "Almond Milk")),
                SpecializedMeal("Lunch", "Turkey & Hummus Whole Wheat Wrap", "Balanced macros for joint and tendon health.", 650, 42, 60, 16, listOf("180g Turkey Slice", "1 Whole Wheat Wrap", "2 tbsp Hummus", "Spinach & Tomato")),
                SpecializedMeal("Dinner", "Tofu & Mixed Veggie Quinoa Stir-Fry", "Plant-rich protein and anti-inflammatory nutrients.", 600, 35, 70, 14, listOf("200g Firm Tofu", "1 cup Quinoa", "Mixed Bell Peppers", "1 tbsp Sesame Oil")),
                SpecializedMeal("Snack", "Greek Yogurt with Chia Seeds & Berries", "Sustained muscle maintenance.", 500, 30, 45, 10, listOf("1.2 cups Greek Yogurt", "1 tbsp Chia Seeds", "1/2 cup Blueberries"))
            )
        ),

        // 🧘 YOGA & RECOVERY
        SpecializedPlan(
            discipline = SpecializedDiscipline.YOGA,
            targetCalories = 2000,
            proteinPercent = 25,
            carbsPercent = 50,
            fatPercent = 25,
            focusDescription = "Enhances joint longevity, spinal decompression, deep hip openers, and anti-inflammatory nervous system balance.",
            workout = Workout(
                id = "plan_yoga_workout",
                title = "Vinyasa Flow & Deep Hip Mobility",
                durationMinutes = 40,
                difficulty = "Beginner",
                aiRationale = "Promotes parasympathetic nervous system dominance and joint synovial fluid lubrication.",
                exercises = listOf(
                    WorkoutExercise(
                        exercise = Exercise("yg_1", "Sun Salutation Vinyasa Flow", "Full Body", "Mat", "Inhale upward dog, exhale downward dog in rhythmic breath cadence."),
                        sets = listOf(WorkoutSet(5, 0f), WorkoutSet(5, 0f), WorkoutSet(5, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("yg_2", "Warrior II to Triangle Pose", "Legs", "Mat", "Sink into lead leg, extend arms parallel, and rotate side body skyward."),
                        sets = listOf(WorkoutSet(8, 0f), WorkoutSet(8, 0f), WorkoutSet(8, 0f))
                    ),
                    WorkoutExercise(
                        exercise = Exercise("yg_3", "Pigeon Pose Hip Opener", "Legs", "Mat", "Square hips to mat and fold forward over front shin for deep glute stretch."),
                        sets = listOf(WorkoutSet(60, 0f), WorkoutSet(60, 0f))
                    )
                )
            ),
            meals = listOf(
                SpecializedMeal("Breakfast", "Avocado & Seed Toast with Green Detox Juice", "High fiber and healthy fats for cellular recovery.", 500, 18, 55, 20, listOf("1/2 Avocado", "2 slices Sourdough Bread", "1 tbsp Pumpkin Seeds", "Green Juice")),
                SpecializedMeal("Lunch", "Nourishing Buddha Bowl with Tahini Dressing", "Plant-based nutrient density.", 600, 24, 75, 18, listOf("1 cup Chickpeas", "1 cup Brown Rice", "Steamed Kale & Roasted Carrots", "2 tbsp Tahini")),
                SpecializedMeal("Dinner", "Lentil & Turmeric Vegetable Stew", "Anti-inflammatory spices for muscle recovery.", 550, 28, 70, 12, listOf("1.5 cups Cooked Lentils", "Turmeric & Ginger Broth", "Chopped Veggies", "1 slice Whole Grain Bread")),
                SpecializedMeal("Snack", "Golden Turmeric Latte with Almond Butter Toast", "Nervous system relaxation before sleep.", 350, 12, 35, 10, listOf("1 cup Warm Almond Milk", "1/2 tsp Turmeric & Cinnamon", "1 slice Toast with Almond Butter"))
            )
        )
    )

    fun getPlan(discipline: SpecializedDiscipline): SpecializedPlan {
        return plans.first { it.discipline == discipline }
    }
}
