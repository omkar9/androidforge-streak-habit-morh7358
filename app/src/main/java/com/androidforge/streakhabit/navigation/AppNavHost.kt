package com.androidforge.streakhabit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androidforge.streakhabit.core.common.Constants
import com.androidforge.streakhabit.presentation.addedit.AddEditHabitScreen
import com.androidforge.streakhabit.presentation.dashboard.DashboardScreen
import com.androidforge.streakhabit.presentation.detail.HabitDetailScreen
import com.androidforge.streakhabit.presentation.settings.SettingsScreen
import com.androidforge.streakhabit.presentation.navigation.Screen // Using the Screen sealed class from presentation layer

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                navigateToCreateHabit = { navController.navigate(Screen.AddEditHabit.createRoute()) },
                navigateToHabitDetail = { habitId -> navController.navigate(Screen.HabitDetail.createRoute(habitId)) },
                navigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(
            route = Screen.AddEditHabit.route,
            arguments = listOf(
                navArgument(Constants.HABIT_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = Constants.DEFAULT_HABIT_ID
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong(Constants.HABIT_ID_ARG)
            AddEditHabitScreen(
                habitId = habitId,
                onHabitSaved = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(
                navArgument(Constants.HABIT_ID_ARG) {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong(Constants.HABIT_ID_ARG)
            requireNotNull(habitId) { "habitId argument not found" }
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                navigateToEditHabit = { id -> navController.navigate(Screen.AddEditHabit.createRoute(id)) }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                navigateToDashboard = {
                    // Navigate to dashboard and clear backstack up to dashboard, then re-navigate to ensure it's on top
                    navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                    navController.navigate(Screen.Dashboard.route) { // Ensure dashboard is the top of the stack
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}