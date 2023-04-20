package dev.ridill.mym.core.navigation.screenSpecs

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.core.util.logD
import dev.ridill.mym.settings.presentation.settings.SettingsScreenContent
import dev.ridill.mym.settings.presentation.settings.SettingsViewModel

object SettingsScreenSpec : BottomBarSpec {
    override val route: String = "settings"

    override val label: Int = R.string.destination_settings

    override val icon: ImageVector = Icons.Outlined.Settings

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: SettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        val googleAccountSelectionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                logD { "Activity Result - ${it.resultCode}" }
                logD { "Activity Result - ${it.data?.extras.toString()}" }
                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.onGoogleAccountSelected(it)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_google_account_linking_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        LaunchedEffect(snackbarController, context, viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    SettingsViewModel.SettingsEvent.LaunchBackupExportPathSelector -> {

                    }

                    is SettingsViewModel.SettingsEvent.LaunchGoogleAccountSelection -> {
                        googleAccountSelectionLauncher.launch(event.intent)
                    }

                    SettingsViewModel.SettingsEvent.RequestSmsPermission -> {

                    }

                    is SettingsViewModel.SettingsEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(
                            event.message.asString(context),
                            event.error
                        )
                    }
                }
            }
        }

        SettingsScreenContent(
            snackbarController = snackbarController,
            context = context,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            navigateToNotificationSettings = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
    }
}