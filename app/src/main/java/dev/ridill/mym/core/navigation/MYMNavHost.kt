package dev.ridill.mym.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ridill.mym.core.navigation.screenSpecs.GraphSpec
import dev.ridill.mym.core.navigation.screenSpecs.NavDestination
import dev.ridill.mym.core.navigation.screenSpecs.ScreenSpec

@Composable
fun MYMNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    require(NavDestination.allDestinations.isNotEmpty()) { "Nav Graph must contain at least 1 destination." }
    NavHost(
        navController = navController,
        startDestination = NavDestination.allDestinations.values.first().route,
        modifier = modifier
    ) {
        NavDestination.allDestinations.values.forEach { destination ->
            when (destination) {
                is ScreenSpec -> addScreen(destination, navController)
                is GraphSpec -> addGraph(destination, navController)
            }
        }
    }
}

private fun NavGraphBuilder.addScreen(
    spec: ScreenSpec,
    navController: NavHostController
) {
    composable(
        route = spec.route,
        arguments = spec.arguments,
        deepLinks = spec.deepLinks
    ) { navBackStackEntry ->
        spec.Content(
            navController = navController,
            navBackStackEntry = navBackStackEntry
        )
    }
}

private fun NavGraphBuilder.addGraph(
    spec: GraphSpec,
    navController: NavHostController
) {
    require(spec.children.isNotEmpty()) { "Navigation Graph must have at least 1 child destination" }
    navigation(
        route = spec.route,
        startDestination = spec.startDestination.route
    ) {
        spec.children.forEach { childSpec ->
            when (childSpec) {
                is ScreenSpec -> addScreen(childSpec, navController)
                is GraphSpec -> addGraph(childSpec, navController)
            }
        }
    }
}