package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.dashboard.presentation.DashboardScreen
import dev.ridill.mym.dashboard.presentation.DashboardViewModel

object DashboardScreenSpec : ScreenSpec {

    override val route: String = "dashboard"

    override val label: Int = R.string.app_name

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: DashboardViewModel = hiltViewModel(navBackStackEntry)

        DashboardScreen(
            viewModel = viewModel
        )
    }
}