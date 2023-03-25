package dev.ridill.mym.core.navigation.screenSpecs

sealed interface NavDestination {

    companion object {
        val allDestinations = listOf<NavDestination>(
            DashboardScreenSpec
        ).associateBy(NavDestination::route)
    }

    val route: String
}