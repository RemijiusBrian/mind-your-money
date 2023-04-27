package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomBarSpec : ScreenSpec {

    companion object {
        val bottomBarDestinations: List<BottomBarSpec>
            get() = NavDestination.allDestinations.values
                .filterIsInstance<BottomBarSpec>()
    }

    val navRoute: String get() = route

    val icon: ImageVector
}