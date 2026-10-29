package com.utsav.kinetiq.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.utsav.kinetiq.domain.model.Workout
import com.utsav.kinetiq.navigation.*
import com.utsav.kinetiq.ui.coach.AICoachScreen
import com.utsav.kinetiq.ui.coach.AICoachViewModel
import com.utsav.kinetiq.ui.home.HomeScreen
import com.utsav.kinetiq.ui.profile.ProfileScreen
import com.utsav.kinetiq.ui.profile.ProfileViewModel
import com.utsav.kinetiq.ui.recipe.RecipesScreen
import com.utsav.kinetiq.ui.recipe.RecipesViewModel
import com.utsav.kinetiq.ui.theme.KinetiqBlack
import com.utsav.kinetiq.ui.theme.KinetiqCharcoal
import com.utsav.kinetiq.ui.theme.KinetiqGray
import com.utsav.kinetiq.ui.theme.KinetiqLime
import com.utsav.kinetiq.ui.vision.VisionScreen
import com.utsav.kinetiq.ui.workout.WorkoutHubScreen
import com.utsav.kinetiq.ui.workout.WorkoutViewModel

sealed class BottomNavItem(val route: Any, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(HomeRoute, Icons.Default.Home, "Home")
    object Workout : BottomNavItem(WorkoutRoute, Icons.Default.FitnessCenter, "Workout")
    object Recipes : BottomNavItem(RecipesRoute, Icons.Default.RestaurantMenu, "Recipes")
    object Vision : BottomNavItem(VisionRoute, Icons.Default.Visibility, "Vision")
    object Coach : BottomNavItem(CoachRoute, Icons.Default.Psychology, "Coach")
    object Profile : BottomNavItem(ProfileRoute, Icons.Default.Person, "Profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Workout,
    BottomNavItem.Recipes,
    BottomNavItem.Vision,
    BottomNavItem.Coach,
    BottomNavItem.Profile
)

@Composable
fun MainScreen(
    onNavigateToGenerator: () -> Unit,
    onNavigateToTracker: (Workout) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    onNavigateToMealScanner: () -> Unit = {},
    onNavigateToSpecializedPlans: () -> Unit = {},
    onNavigateToPromoShowcase: () -> Unit = {},
    onSignOut: () -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
    aiCoachViewModel: AICoachViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    recipesViewModel: RecipesViewModel,
    homeViewModel: com.utsav.kinetiq.ui.home.HomeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = KinetiqBlack,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = KinetiqLime.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    color = KinetiqCharcoal.copy(alpha = 0.95f),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            colors = listOf(
                                KinetiqLime.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.08f),
                                KinetiqLime.copy(alpha = 0.15f)
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true

                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.2f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "nav_scale"
                            )

                            val iconColor by animateColorAsState(
                                targetValue = if (selected) KinetiqLime else KinetiqGray,
                                animationSpec = tween(250),
                                label = "nav_color"
                            )

                            val activeBgAlpha by animateFloatAsState(
                                targetValue = if (selected) 0.15f else 0f,
                                animationSpec = tween(250),
                                label = "nav_bg"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(KinetiqLime.copy(alpha = activeBgAlpha))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .scale(iconScale)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = iconColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.97f, animationSpec = tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.97f, animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.97f, animationSpec = tween(250)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.97f, animationSpec = tween(200)) }
        ) {
            composable<HomeRoute> { 
                HomeScreen(
                    onWorkoutStart = {
                        val mockWorkout = Workout(
                            id = "today_suggested",
                            title = "Chest & Triceps",
                            durationMinutes = 45,
                            difficulty = "Intermediate",
                            exercises = emptyList()
                        )
                        onNavigateToTracker(mockWorkout)
                    },
                    onNavigateToRecipes = {
                        navController.navigate(RecipesRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToMealScanner = onNavigateToMealScanner,
                    onNavigateToSpecializedPlans = onNavigateToSpecializedPlans,
                    onNavigateToPromoShowcase = onNavigateToPromoShowcase,
                    viewModel = homeViewModel
                )
            }
            composable<WorkoutRoute> { 
                WorkoutHubScreen(
                    onNavigateToGenerator = onNavigateToGenerator,
                    onNavigateToTracker = onNavigateToTracker,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToSpecializedPlans = onNavigateToSpecializedPlans,
                    viewModel = workoutViewModel
                )
            }
            composable<RecipesRoute> {
                RecipesScreen(
                    onRecipeClick = onNavigateToRecipeDetail,
                    viewModel = recipesViewModel
                )
            }
            composable<VisionRoute> { VisionScreen() }
            composable<CoachRoute> { 
                AICoachScreen(viewModel = aiCoachViewModel) 
            }
            composable<ProfileRoute> { 
                ProfileScreen(
                    onSignOut = onSignOut,
                    viewModel = profileViewModel
                ) 
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.displaySmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        )
    }
}

