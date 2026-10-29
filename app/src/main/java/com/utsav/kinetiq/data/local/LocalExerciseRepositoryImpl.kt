package com.utsav.kinetiq.data.local

import com.utsav.kinetiq.data.local.dao.KinetiqDao
import com.utsav.kinetiq.data.local.entities.toDomain
import com.utsav.kinetiq.data.local.entities.toEntity
import com.utsav.kinetiq.domain.model.*
import com.utsav.kinetiq.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalExerciseRepositoryImpl(
    private val dao: KinetiqDao
) : ExerciseRepository {

    override fun getExercises(): Flow<List<Exercise>> = dao.getAllExercises().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun getExerciseById(id: String): Flow<Exercise?> = dao.getExerciseById(id).map { 
        it?.toDomain() 
    }

    override fun getWorkouts(): Flow<List<Workout>> = dao.getAllWorkouts().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun getWorkoutById(id: String): Flow<Workout?> = dao.getWorkoutById(id).map { 
        it?.toDomain() 
    }

    override suspend fun saveWorkout(workout: Workout) {
        dao.insertWorkout(workout.toEntity())
    }

    override fun getSessions(): Flow<List<WorkoutSession>> = dao.getAllSessions().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun saveSession(session: WorkoutSession) {
        dao.insertSession(session.toEntity())
    }

    suspend fun seedDatabase() {
        if (dao.getAllExercises().first().isEmpty()) {
            val exercises = listOf(
                // Chest
                Exercise("ex_1", "Push-up", "Chest", "Bodyweight", "Keep body in a straight plank line. Lower chest to floor and push up with power."),
                Exercise("ex_2", "Incline Barbell Bench Press", "Chest", "Barbell", "Set bench to 30 degrees. Lower bar to upper chest and press vertically."),
                Exercise("ex_3", "Flat Dumbbell Press", "Chest", "Dumbbells", "Lie on bench. Lower dumbbells with elbows at 45 degrees, then press together."),
                Exercise("ex_4", "Chest Dips", "Chest", "Parallel Bars", "Lean forward slightly. Lower until elbows reach 90 degrees and push back up."),
                Exercise("ex_5", "Cable Chest Fly", "Chest", "Cable Machine", "Bring handles together in front of chest in a hugging motion, squeezing pectorals."),
                Exercise("ex_39", "Incline Dumbbell Fly", "Chest", "Dumbbells", "Lie on incline bench. Lower weights out to sides with slight elbow bend, then hug back up."),

                // Back
                Exercise("ex_6", "Barbell Deadlift", "Back", "Barbell", "Hinge at hips, keep spine neutral. Pull barbell up legs until standing tall."),
                Exercise("ex_7", "Pull-up", "Back", "Pull-up Bar", "Grip bar shoulder-width. Pull chest up to bar, engaging lats and upper back."),
                Exercise("ex_8", "Lat Pulldown", "Back", "Cable Machine", "Pull bar down to upper chest while squeezing shoulder blades together."),
                Exercise("ex_9", "Barbell Bent-Over Row", "Back", "Barbell", "Hinge torso at 45 degrees. Row bar to lower belly button with elbows close."),
                Exercise("ex_10", "Seated Cable Row", "Back", "Cable Machine", "Pull handle to abdomen with chest tall and shoulder blades retracted."),
                Exercise("ex_47", "Single-Arm Dumbbell Row", "Back", "Dumbbells", "Rest knee on bench. Row heavy dumbbell to hip, squeezing lat at top."),
                Exercise("ex_48", "Straight-Arm Cable Pulldown", "Back", "Cable Machine", "Keep arms extended straight. Sweep bar down to thighs using lat force."),
                Exercise("ex_41", "Superman Back Extension", "Back", "Bodyweight", "Lie on stomach. Simultaneously raise arms and legs off floor, squeezing lower back."),

                // Legs
                Exercise("ex_11", "Barbell Back Squat", "Legs", "Barbell", "Bar across upper traps. Lower hips below parallel, driving through heels to stand."),
                Exercise("ex_12", "Romanian Deadlift", "Legs", "Barbell / Dumbbells", "Soft knees, hinge hips back until hamstrings stretch. Drive hips forward."),
                Exercise("ex_13", "Dumbbell Walking Lunge", "Legs", "Dumbbells", "Step forward, lower back knee near floor. Push off front heel to step."),
                Exercise("ex_14", "Leg Extension", "Legs", "Leg Extension Machine", "Extend legs until quads fully contract. Lower with control."),
                Exercise("ex_15", "Lying Leg Curl", "Legs", "Leg Curl Machine", "Curl pad toward glutes focusing on hamstring contraction."),
                Exercise("ex_16", "Standing Calf Raise", "Legs", "Machine / Step", "Raise heels as high as possible on toes, pause, and lower below step."),
                Exercise("ex_35", "Barbell Hip Thrust", "Legs", "Barbell / Bench", "Upper back resting on bench. Drive loaded barbell up with hips, squeezing glutes hard at top."),
                Exercise("ex_36", "Goblet Squat", "Legs", "Dumbbell / Kettlebell", "Hold heavy weight at chest. Squat deep while keeping torso upright and knees out."),
                Exercise("ex_49", "Bulgarian Split Squat", "Legs", "Dumbbells / Bench", "Rear foot elevated on bench. Lower hips until front thigh is parallel to floor."),
                Exercise("ex_50", "Sumo Deadlift", "Legs", "Barbell", "Wide stance with toes pointed out. Grip bar inside knees, pull with hips and glutes."),

                // Shoulders
                Exercise("ex_17", "Overhead Shoulder Press", "Shoulders", "Barbell / Dumbbells", "Press weights overhead until arms lock out with core engaged."),
                Exercise("ex_18", "Dumbbell Lateral Raise", "Shoulders", "Dumbbells", "Raise dumbbells out to sides until parallel with floor, leading with elbows."),
                Exercise("ex_19", "Face Pull", "Shoulders", "Cable Machine", "Pull rope attachment toward nose, flaring elbows out to target rear delts."),
                Exercise("ex_20", "Dumbbell Rear Delt Fly", "Shoulders", "Dumbbells", "Bend over 90 degrees. Raise weights outward squeezing posterior deltoids."),
                Exercise("ex_37", "Arnold Dumbbell Press", "Shoulders", "Dumbbells", "Start with palms facing chest. Rotate palms outward as you press weights overhead."),

                // Arms
                Exercise("ex_21", "Barbell Bicep Curl", "Arms", "Barbell", "Keep elbows pinned to sides. Curl bar up toward shoulders."),
                Exercise("ex_22", "Dumbbell Hammer Curl", "Arms", "Dumbbells", "Palms facing each other. Curl dumbbells up to emphasize brachialis."),
                Exercise("ex_23", "Tricep Cable Pushdown", "Arms", "Cable Machine", "Keep upper arms stationary. Extend elbows downward fully."),
                Exercise("ex_24", "Skullcrusher / Lying Extension", "Arms", "Barbell / Dumbbells", "Lower bar toward forehead by bending elbows, then extend."),
                Exercise("ex_25", "Bench Tricep Dips", "Arms", "Bench", "Lower body off bench edge until elbows bend 90 degrees, then push up."),
                Exercise("ex_45", "Close-Grip Bench Press", "Arms", "Barbell", "Grip barbell shoulder-width. Lower to lower chest, keeping elbows tucked close."),
                Exercise("ex_46", "EZ-Bar Preacher Curl", "Arms", "Preacher Bench", "Rest triceps on pad. Curl bar up to full contraction without swinging."),

                // Core
                Exercise("ex_26", "Hanging Leg Raise", "Core", "Pull-up Bar", "Hang from bar. Raise legs up to 90 degrees using lower abdominal strength."),
                Exercise("ex_27", "Russian Twists", "Core", "Bodyweight / Plate", "Sit in V-position. Rotate torso side to side, touching weight to floor."),
                Exercise("ex_28", "Ab Wheel Rollout", "Core", "Ab Wheel", "Kneel on floor. Roll wheel forward keeping core braced, then pull back."),
                Exercise("ex_29", "Forearm Plank", "Core", "Bodyweight", "Hold straight plank on forearms, bracing glutes and abs continuously."),
                Exercise("ex_40", "Cable Woodchopper", "Core", "Cable Machine", "Pull cable diagonally across torso in explosive twisting motion."),
                Exercise("ex_44", "Side Plank", "Core", "Bodyweight", "Lie on side on forearm. Lift hips to form straight diagonal line and hold."),

                // Cardio / HIIT / Functional
                Exercise("ex_30", "Burpees", "Full Body", "Bodyweight", "Drop into pushup position, jump feet in, and jump explosively overhead."),
                Exercise("ex_31", "Mountain Climbers", "Full Body", "Bodyweight", "In plank position, rapidly drive knees alternating toward chest."),
                Exercise("ex_32", "Kettlebell Swing", "Full Body", "Kettlebell", "Hinge hips back and snap hips forward to swing kettlebell to shoulder height."),
                Exercise("ex_33", "Jumping Jacks", "Full Body", "Bodyweight", "Jump feet out while swinging arms overhead, then return to center."),
                Exercise("ex_34", "Plyometric Box Jump", "Full Body", "Plyo Box", "Quarter squat and jump explosively onto box landing softly."),
                Exercise("ex_38", "Heavy Farmer's Walk", "Full Body", "Dumbbells / Kettlebells", "Hold heavy weights at sides and walk with tall posture and braced core."),
                Exercise("ex_42", "Plyometric Jump Squats", "Full Body", "Bodyweight", "Squat deep and spring explosively into the air, landing softly."),
                Exercise("ex_43", "High Knees Running", "Full Body", "Bodyweight", "Run in place driving knees up to hip height rapidly.")
            )
            dao.insertExercises(exercises.map { it.toEntity() })
            
            val mockWorkouts = listOf(
                Workout(
                    id = "w_1",
                    title = "Full Body Anabolic Builder",
                    durationMinutes = 45,
                    difficulty = "Intermediate",
                    aiRationale = "Combines compound multi-joint movements for maximum muscle fiber recruitment.",
                    exercises = listOf(
                        WorkoutExercise(exercises[10], listOf(WorkoutSet(10, 60f), WorkoutSet(10, 60f), WorkoutSet(10, 60f))), // Squat
                        WorkoutExercise(exercises[2], listOf(WorkoutSet(10, 24f), WorkoutSet(10, 24f), WorkoutSet(10, 24f))),  // Flat DB Press
                        WorkoutExercise(exercises[8], listOf(WorkoutSet(10, 50f), WorkoutSet(10, 50f), WorkoutSet(10, 50f))),  // BB Row
                        WorkoutExercise(exercises[16], listOf(WorkoutSet(10, 16f), WorkoutSet(10, 16f), WorkoutSet(10, 16f)))  // Shoulder Press
                    )
                ),
                Workout(
                    id = "w_2",
                    title = "Push Day Hypertrophy",
                    durationMinutes = 50,
                    difficulty = "Advanced",
                    aiRationale = "Targets Chest, Shoulders, and Triceps with optimal progressive overload.",
                    exercises = listOf(
                        WorkoutExercise(exercises[1], listOf(WorkoutSet(8, 70f), WorkoutSet(8, 70f), WorkoutSet(8, 70f))),   // Incline Press
                        WorkoutExercise(exercises[3], listOf(WorkoutSet(12, 0f), WorkoutSet(12, 0f), WorkoutSet(12, 0f))),   // Dips
                        WorkoutExercise(exercises[16], listOf(WorkoutSet(10, 20f), WorkoutSet(10, 20f), WorkoutSet(10, 20f))), // Shoulder Press
                        WorkoutExercise(exercises[17], listOf(WorkoutSet(15, 10f), WorkoutSet(15, 10f), WorkoutSet(15, 10f))), // Lateral Raise
                        WorkoutExercise(exercises[22], listOf(WorkoutSet(12, 25f), WorkoutSet(12, 25f), WorkoutSet(12, 25f)))  // Cable Pushdown
                    )
                ),
                Workout(
                    id = "w_3",
                    title = "Pull & Back Power",
                    durationMinutes = 45,
                    difficulty = "Intermediate",
                    aiRationale = "Maximizes lats, upper back thickness, and bicep peak development.",
                    exercises = listOf(
                        WorkoutExercise(exercises[5], listOf(WorkoutSet(5, 100f), WorkoutSet(5, 100f), WorkoutSet(5, 100f))), // Deadlift
                        WorkoutExercise(exercises[6], listOf(WorkoutSet(8, 0f), WorkoutSet(8, 0f), WorkoutSet(8, 0f))),      // Pull-up
                        WorkoutExercise(exercises[9], listOf(WorkoutSet(12, 45f), WorkoutSet(12, 45f), WorkoutSet(12, 45f))), // Seated Row
                        WorkoutExercise(exercises[18], listOf(WorkoutSet(15, 15f), WorkoutSet(15, 15f), WorkoutSet(15, 15f))),// Face Pull
                        WorkoutExercise(exercises[20], listOf(WorkoutSet(10, 14f), WorkoutSet(10, 14f), WorkoutSet(10, 14f))) // Bicep Curl
                    )
                ),
                Workout(
                    id = "w_4",
                    title = "Leg Day Quad & Hamstring Crusher",
                    durationMinutes = 55,
                    difficulty = "Advanced",
                    aiRationale = "Combines heavy quad squatting with posterior chain hamstring isolation.",
                    exercises = listOf(
                        WorkoutExercise(exercises[10], listOf(WorkoutSet(8, 80f), WorkoutSet(8, 80f), WorkoutSet(8, 80f))), // Squat
                        WorkoutExercise(exercises[11], listOf(WorkoutSet(10, 60f), WorkoutSet(10, 60f), WorkoutSet(10, 60f))),// RDL
                        WorkoutExercise(exercises[12], listOf(WorkoutSet(12, 18f), WorkoutSet(12, 18f), WorkoutSet(12, 18f))),// Lunges
                        WorkoutExercise(exercises[13], listOf(WorkoutSet(15, 40f), WorkoutSet(15, 40f), WorkoutSet(15, 40f))),// Leg Ext
                        WorkoutExercise(exercises[15], listOf(WorkoutSet(20, 50f), WorkoutSet(20, 50f), WorkoutSet(20, 50f))) // Calf Raise
                    )
                ),
                Workout(
                    id = "w_5",
                    title = "HIIT Fat Burn & Conditioning",
                    durationMinutes = 30,
                    difficulty = "Intermediate",
                    aiRationale = "High heart rate metabolic conditioning for maximal post-exercise oxygen consumption.",
                    exercises = listOf(
                        WorkoutExercise(exercises[29], listOf(WorkoutSet(15, 0f), WorkoutSet(15, 0f), WorkoutSet(15, 0f))), // Burpees
                        WorkoutExercise(exercises[31], listOf(WorkoutSet(20, 20f), WorkoutSet(20, 20f), WorkoutSet(20, 20f))),// Kettlebell Swing
                        WorkoutExercise(exercises[30], listOf(WorkoutSet(30, 0f), WorkoutSet(30, 0f), WorkoutSet(30, 0f))), // Mountain Climbers
                        WorkoutExercise(exercises[32], listOf(WorkoutSet(40, 0f), WorkoutSet(40, 0f), WorkoutSet(40, 0f)))  // Jumping Jacks
                    )
                ),
                Workout(
                    id = "w_6",
                    title = "Six-Pack Abs & Core Sculpt",
                    durationMinutes = 20,
                    difficulty = "Beginner",
                    aiRationale = "Targeted core endurance and intra-abdominal bracing stability.",
                    exercises = listOf(
                        WorkoutExercise(exercises[25], listOf(WorkoutSet(12, 0f), WorkoutSet(12, 0f), WorkoutSet(12, 0f))), // Hanging Leg Raise
                        WorkoutExercise(exercises[26], listOf(WorkoutSet(20, 10f), WorkoutSet(20, 10f), WorkoutSet(20, 10f))),// Russian Twists
                        WorkoutExercise(exercises[27], listOf(WorkoutSet(10, 0f), WorkoutSet(10, 0f), WorkoutSet(10, 0f))), // Ab Wheel
                        WorkoutExercise(exercises[28], listOf(WorkoutSet(60, 0f), WorkoutSet(60, 0f), WorkoutSet(60, 0f)))  // Plank
                    )
                ),
                Workout(
                    id = "w_7",
                    title = "Anabolic Chest & Tricep Blast",
                    durationMinutes = 40,
                    difficulty = "Intermediate",
                    aiRationale = "High mechanical tension pressing followed by isolation flyes and tricep extension.",
                    exercises = listOf(
                        WorkoutExercise(exercises[2], listOf(WorkoutSet(10, 28f), WorkoutSet(10, 28f), WorkoutSet(10, 28f))), // Flat DB Press
                        WorkoutExercise(exercises[1], listOf(WorkoutSet(10, 60f), WorkoutSet(10, 60f), WorkoutSet(10, 60f))), // Incline Press
                        WorkoutExercise(exercises[5], listOf(WorkoutSet(12, 15f), WorkoutSet(12, 15f), WorkoutSet(12, 15f))), // Cable Fly
                        WorkoutExercise(exercises[22], listOf(WorkoutSet(15, 30f), WorkoutSet(15, 30f), WorkoutSet(15, 30f))) // Cable Pushdown
                    )
                ),
                Workout(
                    id = "w_8",
                    title = "V-Taper Back & Bicep Sculpt",
                    durationMinutes = 45,
                    difficulty = "Intermediate",
                    aiRationale = "Develops lat width and upper back thickness with peak bicep contraction.",
                    exercises = listOf(
                        WorkoutExercise(exercises[7], listOf(WorkoutSet(10, 55f), WorkoutSet(10, 55f), WorkoutSet(10, 55f))), // Lat Pulldown
                        WorkoutExercise(exercises[8], listOf(WorkoutSet(10, 50f), WorkoutSet(10, 50f), WorkoutSet(10, 50f))), // BB Row
                        WorkoutExercise(exercises[11], listOf(WorkoutSet(12, 20f), WorkoutSet(12, 20f), WorkoutSet(12, 20f))),// Hammer Curl
                        WorkoutExercise(exercises[20], listOf(WorkoutSet(12, 16f), WorkoutSet(12, 16f), WorkoutSet(12, 16f))) // BB Curl
                    )
                ),
                Workout(
                    id = "w_9",
                    title = "Glute & Leg Powerhouse",
                    durationMinutes = 50,
                    difficulty = "Advanced",
                    aiRationale = "Focuses on hip extension power, glute max activation, and quad conditioning.",
                    exercises = listOf(
                        WorkoutExercise(exercises[18], listOf(WorkoutSet(10, 90f), WorkoutSet(10, 90f), WorkoutSet(10, 90f))),// Hip Thrust
                        WorkoutExercise(exercises[10], listOf(WorkoutSet(10, 70f), WorkoutSet(10, 70f), WorkoutSet(10, 70f))),// Squat
                        WorkoutExercise(exercises[12], listOf(WorkoutSet(12, 20f), WorkoutSet(12, 20f), WorkoutSet(12, 20f))),// Walking Lunge
                        WorkoutExercise(exercises[14], listOf(WorkoutSet(15, 35f), WorkoutSet(15, 35f), WorkoutSet(15, 35f))) // Leg Curl
                    )
                ),
                Workout(
                    id = "w_10",
                    title = "Dumbbell-Only Home Full Body",
                    durationMinutes = 35,
                    difficulty = "Beginner",
                    aiRationale = "Full body resistance session requiring only a pair of dumbbells.",
                    exercises = listOf(
                        WorkoutExercise(exercises[19], listOf(WorkoutSet(12, 20f), WorkoutSet(12, 20f), WorkoutSet(12, 20f))),// Goblet Squat
                        WorkoutExercise(exercises[2], listOf(WorkoutSet(12, 18f), WorkoutSet(12, 18f), WorkoutSet(12, 18f))), // DB Press
                        WorkoutExercise(exercises[13], listOf(WorkoutSet(12, 16f), WorkoutSet(12, 16f), WorkoutSet(12, 16f))),// DB Row
                        WorkoutExercise(exercises[16], listOf(WorkoutSet(12, 14f), WorkoutSet(12, 14f), WorkoutSet(12, 14f))) // DB Shoulder Press
                    )
                )
            )
            mockWorkouts.forEach { dao.insertWorkout(it.toEntity()) }
        }
    }
}
