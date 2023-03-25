package dev.ridill.mym.core.navigation.screenSpecs

sealed interface GraphSpec : NavDestination {

    val children: List<NavDestination>

    val startDestination: NavDestination
        get() = children.first()
}