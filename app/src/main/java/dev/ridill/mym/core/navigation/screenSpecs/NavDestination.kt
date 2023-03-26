package dev.ridill.mym.core.navigation.screenSpecs

sealed interface NavDestination {

    companion object {
        val allDestinations = listOf<NavDestination>(
            DashboardScreenSpec,
            ExpenseDetailsScreenSpec
        ).associateBy(NavDestination::route)
    }

    val route: String
}

const val ARG_INVALID_ID_LONG = -1L