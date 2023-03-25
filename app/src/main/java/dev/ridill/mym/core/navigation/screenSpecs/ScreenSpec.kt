package dev.ridill.mym.core.navigation.screenSpecs

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController

sealed interface ScreenSpec : NavDestination {
    @get:StringRes
    val label: Int

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()

    @Composable
    fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry)
}