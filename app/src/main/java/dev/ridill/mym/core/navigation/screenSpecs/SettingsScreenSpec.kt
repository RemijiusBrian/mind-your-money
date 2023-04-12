package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.settings.presentation.settings.SettingsScreenContent
import dev.ridill.mym.settings.presentation.settings.SettingsState
import dev.ridill.mym.settings.presentation.settings.SettingsViewModel

object SettingsScreenSpec : BottomBarSpec {
    override val route: String = "settings"

    override val label: Int = R.string.destination_settings

    override val icon: ImageVector = Icons.Outlined.Settings

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: SettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.observeAsState(SettingsState.INITIAL)

        SettingsScreenContent(
            snackbarController = rememberSnackbarController(),
            context = LocalContext.current,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            navigateToNotificationSettings = {}
        )
    }
}