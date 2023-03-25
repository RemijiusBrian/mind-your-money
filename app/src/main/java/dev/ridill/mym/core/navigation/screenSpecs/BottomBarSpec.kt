package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomBarSpec : ScreenSpec {

    companion object {
        val bottomBarDestinations = NavDestination.allDestinations.values
            .filterIsInstance<BottomBarSpec>()
    }

    val icon: ImageVector
}