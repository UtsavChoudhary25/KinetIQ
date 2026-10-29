package com.utsav.kinetiq.navigation

import kotlinx.serialization.Serializable

/**
 * Root level navigation destinations
 */
@Serializable
object SplashRoute

@Serializable
object OnboardingRoute

@Serializable
object AuthRoute

@Serializable
object LoginRoute

@Serializable
object SignUpRoute

@Serializable
object ProfileSetupRoute

@Serializable
object MainRoute

/**
 * Main App bottom navigation destinations
 */
@Serializable
object HomeRoute

@Serializable
object WorkoutRoute

@Serializable
object WorkoutGeneratorRoute

@Serializable
object HistoryRoute

@Serializable
object ActiveWorkoutRoute

@Serializable
object VisionRoute

@Serializable
object CoachRoute

@Serializable
object RecipesRoute

@Serializable
data class RecipeDetailRoute(val recipeId: String)

@Serializable
object MealScannerRoute

@Serializable
object SpecializedPlansRoute

@Serializable
object PromoShowcaseRoute

@Serializable
object ProfileRoute
