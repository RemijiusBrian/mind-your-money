package dev.ridill.mym.core.navigation.screenSpecs

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.core.util.isPermanentlyDenied
import dev.ridill.mym.settings.presentation.settings.SettingsScreenContent
import dev.ridill.mym.settings.presentation.settings.SettingsViewModel

object SettingsScreenSpec : BottomBarSpec {

    override val route: String = "settings?$ARG_QUICK_ACTION={$ARG_QUICK_ACTION}"

    override val navRoute: String = "settings"

    override val label: Int = R.string.destination_settings

    override val iconRes: Int = R.drawable.ic_settings

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(ARG_QUICK_ACTION) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )

    fun routeWithArg(action: String? = null): String = buildString {
        append("settings")
        action?.let {
            append("?$ARG_QUICK_ACTION=$it")
        }
    }

    fun getQuickActionFromSavedStateHandle(savedStateHandle: SavedStateHandle): String? =
        savedStateHandle.get<String?>(ARG_QUICK_ACTION)

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: SettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        val smsPermissionState = rememberPermissionState(Manifest.permission.RECEIVE_SMS)
        val smsPermissionGranted by remember {
            derivedStateOf { smsPermissionState.status.isGranted }
        }

        LaunchedEffect(snackbarController, context, viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    SettingsViewModel.SettingsEvent.LaunchBackupExportPathSelector -> {

                    }

                    is SettingsViewModel.SettingsEvent.LaunchGoogleAccountSelection -> {

                    }

                    SettingsViewModel.SettingsEvent.RequestSmsPermission -> {
                        if (smsPermissionState.status.isGranted) return@collect
                        if (smsPermissionState.status.isPermanentlyDenied()) {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                            context.startActivity(intent)
                        } else {
                            smsPermissionState.launchPermissionRequest()
                        }

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
            smsPermissionGranted = smsPermissionGranted,
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

private const val ARG_QUICK_ACTION = "ARG_QUICK_ACTION"
const val ARG_QUICK_ACTION_LIMIT_UPDATE = "ARG_QUICK_ACTION_LIMIT_UPDATE"