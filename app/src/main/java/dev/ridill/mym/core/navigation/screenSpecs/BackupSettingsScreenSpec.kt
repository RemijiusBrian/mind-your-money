package dev.ridill.mym.core.navigation.screenSpecs

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.mym.R
import dev.ridill.mym.application.MYMActivity
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.core.util.logI
import dev.ridill.mym.settings.presentation.backup.BackupSettingsScreenContent
import dev.ridill.mym.settings.presentation.backup.BackupSettingsViewModel
import kotlin.system.exitProcess


object BackupSettingsScreenSpec : ScreenSpec {
    override val route: String = "backup_settings"

    override val label: Int = R.string.destination_backup_settings

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: BackupSettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val snackbarController = rememberSnackbarController()

        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.onGoogleAccountSelected(it)
                }
            }
        )

        LaunchedEffect(viewModel, context, snackbarController) {
            viewModel.events.collect { event ->
                when (event) {
                    is BackupSettingsViewModel.BackupSettingsEvent.StartGoogleSignInIntent -> {
                        googleSignInLauncher.launch(event.intent)
                    }

                    is BackupSettingsViewModel.BackupSettingsEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(
                            message = event.message.asString(context),
                            error = event.isError
                        )
                    }

                    BackupSettingsViewModel.BackupSettingsEvent.RestartApplication -> {
                        logI { "Restarting Application" }
                        val intent = Intent(context, MYMActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                        exitProcess(0)
                    }
                }
            }
        }

        BackupSettingsScreenContent(
            snackbarController = snackbarController,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp
        )
    }
}