package dev.ridill.mym.core.navigation.screenSpecs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import dev.ridill.mym.expenses.presentation.all_expenses.AllExpensesScreenContent
import dev.ridill.mym.expenses.presentation.all_expenses.AllExpensesViewModel

object AllExpensesScreenSpec : BottomBarSpec {
    override val route: String = "all_expenses"

    override val icon: ImageVector = Icons.Default.Money

    override val label: Int = R.string.destination_all_expenses

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: AllExpensesViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()
        val tagInput = viewModel.tagInput.collectAsStateWithLifecycle()

        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Hidden,
                skipHiddenState = false
            )
        )
        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        LaunchedEffect(snackbarController, context, viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    is AllExpensesViewModel.AllExpenseEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(
                            event.message.asString(context),
                            event.isError
                        )
                    }

                    is AllExpensesViewModel.AllExpenseEvent.ToggleTagInput -> {
                        if (event.show) bottomSheetScaffoldState.bottomSheetState.expand()
                        else bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }
        }

        AllExpensesScreenContent(
            snackbarController = snackbarController,
            state = state,
            tagInput = { tagInput.value },
            actions = viewModel,
            navigateUp = navController::navigateUp,
            bottomSheetScaffoldState = bottomSheetScaffoldState
        )
    }
}