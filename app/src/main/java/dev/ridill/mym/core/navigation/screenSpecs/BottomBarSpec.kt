package dev.ridill.mym.core.navigation.screenSpecs

import androidx.annotation.DrawableRes

sealed interface BottomBarSpec : ScreenSpec {

    companion object {
        val bottomBarDestinations: List<BottomBarSpec>
            get() = NavDestination.allDestinations.values
                .filterIsInstance<BottomBarSpec>()
    }

    val navRoute: String get() = route

    @get:DrawableRes
    val iconRes: Int
}