package com.utsav.kinetiq.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.utsav.kinetiq.data.local.GeminiPrefs
import com.utsav.kinetiq.data.local.LocalExerciseRepositoryImpl
import com.utsav.kinetiq.data.local.LocalRecipeRepository
import com.utsav.kinetiq.data.local.db.KinetiqDatabase
import com.utsav.kinetiq.data.remote.FirestoreUserRepositoryImpl
import com.utsav.kinetiq.data.remote.GeminiRepository
import com.utsav.kinetiq.ui.auth.LoginScreen
import com.utsav.kinetiq.ui.auth.SignUpScreen
import com.utsav.kinetiq.ui.coach.AICoachViewModel
import com.utsav.kinetiq.ui.home.HomeViewModel
import com.utsav.kinetiq.ui.main.MainScreen
import com.utsav.kinetiq.ui.promo.PromoShowcaseScreen
import com.utsav.kinetiq.ui.nutrition.MealScannerScreen
import com.utsav.kinetiq.ui.nutrition.MealScannerViewModel
import com.utsav.kinetiq.ui.onboarding.OnboardingScreen
import com.utsav.kinetiq.ui.profile.ProfileSetupScreen
import com.utsav.kinetiq.ui.profile.ProfileViewModel
import com.utsav.kinetiq.ui.recipe.RecipeDetailScreen
import com.utsav.kinetiq.ui.recipe.RecipesViewModel
import com.utsav.kinetiq.ui.splash.SplashScreen
import com.utsav.kinetiq.ui.workout.HistoryScreen
import com.utsav.kinetiq.ui.workout.WorkoutViewModel
import com.utsav.kinetiq.ui.workout.generator.WorkoutGeneratorScreen
import com.utsav.kinetiq.ui.workout.tracker.ActiveWorkoutScreen

@Composable
fun KinetiqNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,

) {
    val context = LocalContext.current
    
    // AI Configuration
    val geminiPrefs = remember { GeminiPrefs(context) }
    val geminiApiKey = com.utsav.kinetiq.BuildConfig.GEMINI_API_KEY
    val geminiRepository = remember { GeminiRepository(geminiApiKey) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        geminiPrefs.apiKey.collect { key ->
            if (!key.isNullOrBlank()) {
                geminiRepository.updateApiKey(key)
            } else if (geminiApiKey.isNotBlank()) {
                geminiPrefs.saveApiKey(geminiApiKey)
                geminiRepository.updateApiKey(geminiApiKey)
            }
        }
    }

    val repository = remember { 
        LocalExerciseRepositoryImpl(KinetiqDatabase.getDatabase(context).dao()) 
    }

    val recipeRepository = remember { LocalRecipeRepository(context) }
    val shoppingListPrefs = remember { com.utsav.kinetiq.data.local.ShoppingListPrefs(context) }
    val mealPlannerPrefs = remember { com.utsav.kinetiq.data.local.MealPlannerPrefs(context) }
    val recipesViewModel: RecipesViewModel = viewModel(
        factory = RecipesViewModel.provideFactory(recipeRepository, shoppingListPrefs, mealPlannerPrefs, geminiRepository)
    )
    
    val workoutViewModel: WorkoutViewModel = viewModel(
        factory = WorkoutViewModel.provideFactory(repository, geminiRepository, geminiPrefs)
    )
    
    val aiCoachViewModel: AICoachViewModel = viewModel(
        factory = AICoachViewModel.provideFactory(geminiRepository, geminiPrefs)
    )
    
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(context, FirestoreUserRepositoryImpl())
    )

    val homeViewModel: HomeViewModel = viewModel()
    val mealScannerViewModel: MealScannerViewModel = viewModel(
        factory = MealScannerViewModel.provideFactory(geminiRepository, geminiPrefs)
    )

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { (it * 0.2f).toInt() }, animationSpec = tween(350, easing = FastOutSlowInEasing)) +
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { (-it * 0.2f).toInt() }, animationSpec = tween(350, easing = FastOutSlowInEasing)) +
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { (-it * 0.2f).toInt() }, animationSpec = tween(350, easing = FastOutSlowInEasing)) +
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { (it * 0.2f).toInt() }, animationSpec = tween(350, easing = FastOutSlowInEasing)) +
            fadeOut(animationSpec = tween(300))
        }
    ) {
        composable<SplashRoute> {
            SplashScreen { nextRoute ->
                navController.navigate(nextRoute) {
                    popUpTo<SplashRoute> { inclusive = true }
                }
            }
        }

        composable<OnboardingRoute> {
            OnboardingScreen(
                onOnboardingFinished = {
                    navController.navigate(AuthRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            )
        }

        navigation<AuthRoute>(startDestination = LoginRoute) {
            composable<LoginRoute> {
                LoginScreen(
                    onLoginSuccess = {
                        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            kotlinx.coroutines.MainScope().launch {
                                try {
                                    val profileResult = FirestoreUserRepositoryImpl().getUserProfile(user.uid)
                                    val profile = profileResult.getOrNull()
                                    if (profile == null || !profile.onboardingCompleted || profile.height == 0f || profile.weight == 0f) {
                                        navController.navigate(ProfileSetupRoute) {
                                            popUpTo<AuthRoute> { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(MainRoute) {
                                            popUpTo<AuthRoute> { inclusive = true }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    navController.navigate(ProfileSetupRoute) {
                                        popUpTo<AuthRoute> { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            navController.navigate(MainRoute) {
                                popUpTo<AuthRoute> { inclusive = true }
                            }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(SignUpRoute)
                    }
                )
            }
            composable<SignUpRoute> {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate(ProfileSetupRoute) {
                            popUpTo<AuthRoute> { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(LoginRoute)
                    }
                )
            }
        }

        composable<ProfileSetupRoute> {
            ProfileSetupScreen(
                onSetupComplete = {
                    navController.navigate(MainRoute) {
                        popUpTo<ProfileSetupRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<MainRoute> {
            MainScreen(
                onNavigateToGenerator = {
                    navController.navigate(WorkoutGeneratorRoute)
                },
                onNavigateToTracker = { workout ->
                    workoutViewModel.startWorkout(workout)
                    navController.navigate(ActiveWorkoutRoute)
                },
                onNavigateToHistory = {
                    navController.navigate(HistoryRoute)
                },
                onNavigateToRecipeDetail = { recipeId ->
                    navController.navigate(RecipeDetailRoute(recipeId))
                },
                onNavigateToMealScanner = {
                    navController.navigate(MealScannerRoute)
                },
                onNavigateToSpecializedPlans = {
                    navController.navigate(SpecializedPlansRoute)
                },
                onNavigateToPromoShowcase = {
                    navController.navigate(PromoShowcaseRoute)
                },
                onSignOut = {
                    navController.navigate(AuthRoute) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                workoutViewModel = workoutViewModel,
                aiCoachViewModel = aiCoachViewModel,
                profileViewModel = profileViewModel,
                recipesViewModel = recipesViewModel,
                homeViewModel = homeViewModel
            )
        }

        composable<MealScannerRoute> {
            MealScannerScreen(
                onBackClick = { navController.popBackStack() },
                onLogCalories = { calories ->
                    homeViewModel.addLoggedCalories(calories)
                },
                viewModel = mealScannerViewModel
            )
        }

        composable<SpecializedPlansRoute> {
            com.utsav.kinetiq.ui.plans.SpecializedPlansScreen(
                onBackClick = { navController.popBackStack() },
                onStartWorkout = { workout ->
                    workoutViewModel.startWorkout(workout)
                    navController.navigate(ActiveWorkoutRoute)
                },
                recipesViewModel = recipesViewModel
            )
        }

        composable<PromoShowcaseRoute> {
            PromoShowcaseScreen(
                onCloseClick = { navController.popBackStack() },
                onNavigateToVision = {
                    navController.navigate(VisionRoute)
                },
                onNavigateToMealScanner = {
                    navController.navigate(MealScannerRoute)
                },
                onNavigateToCoach = {
                    navController.navigate(CoachRoute)
                },
                onNavigateToWorkouts = {
                    navController.navigate(WorkoutRoute)
                }
            )
        }

        composable<RecipeDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeDetailRoute>()
            RecipeDetailScreen(
                recipeId = route.recipeId,
                onBackClick = { navController.popBackStack() },
                viewModel = recipesViewModel
            )
        }

        composable<WorkoutGeneratorRoute> {
            WorkoutGeneratorScreen(
                onBackClick = { navController.popBackStack() },
                onWorkoutGenerated = {
                    navController.navigate(ActiveWorkoutRoute) {
                        popUpTo<WorkoutGeneratorRoute> { inclusive = true }
                    }
                },
                viewModel = workoutViewModel
            )
        }

        composable<HistoryRoute> {
            HistoryScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = workoutViewModel
            )
        }

        composable<ActiveWorkoutRoute> {
            ActiveWorkoutScreen(
                onFinishClick = {
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                },
                viewModel = workoutViewModel
            )
        }
    }
}
