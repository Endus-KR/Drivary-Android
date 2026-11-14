package com.endus.drivary.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.endus.drivary.presentation.debug.DebugSettingsRoute
import com.endus.drivary.presentation.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

@Serializable
data object DebugSettingsDestination

@Composable
fun DrivaryNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        composable<HomeDestination> {
            HomeRoute(onNavigateToDebugSettings = { navController.navigate(DebugSettingsDestination) })
        }
        composable<DebugSettingsDestination> {
            DebugSettingsRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
