package dev.ridill.mym.core.navigation.screenSpecs

import androidx.annotation.StringRes
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.expenses.presentation.add_edit_expense.AddEditExpenseScreenContent
import dev.ridill.mym.expenses.presentation.add_edit_expense.AddEditExpenseViewModel
import dev.ridill.mym.expenses.presentation.add_edit_expense.EXPENSE_DETAILS_ACTION

object AddEditExpenseScreenSpec : ScreenSpec {

    override val route: String = "add_edit_expense/{$ARG_EXPENSE_ID}"

    override val label: Int = R.string.destination_add_edit_expense

    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink {
            uriPattern = "https://www.mym.ridill.dev/add_edit_expense/{$ARG_EXPENSE_ID}"
        }
    )

    @StringRes
    fun getTitle(isEditMode: Boolean): Int =
        if (isEditMode) R.string.edit_expense else R.string.add_expense

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_EXPENSE_ID) {
            type = NavType.LongType
            nullable = false
            defaultValue = ARG_INVALID_ID_LONG
        }
    )

    fun routeWithArgs(expenseId: Long? = null): String =
        route.replace("{$ARG_EXPENSE_ID}", (expenseId ?: ARG_INVALID_ID_LONG).toString())

    fun getExpenseIdFromSavedStateHandle(savedStateHandle: SavedStateHandle): Long =
        savedStateHandle.get<Long>(ARG_EXPENSE_ID) ?: ARG_INVALID_ID_LONG

    private fun getExpenseIdFromArgs(navBackStackEntry: NavBackStackEntry): Long =
        navBackStackEntry.arguments?.getLong(ARG_EXPENSE_ID, -1L) ?: ARG_INVALID_ID_LONG

    @Composable
    override fun Content(navController: NavHostController, navBackStackEntry: NavBackStackEntry) {
        val viewModel: AddEditExpenseViewModel = hiltViewModel(navBackStackEntry)
        val isEditMode = getExpenseIdFromArgs(navBackStackEntry) > ARG_INVALID_ID_LONG

        val amount = viewModel.amount.collectAsStateWithLifecycle("")
        val note = viewModel.note.collectAsStateWithLifecycle("")
        val state by viewModel.state.collectAsStateWithLifecycle()
        val newTag = viewModel.newTag.collectAsStateWithLifecycle()

        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Hidden,
                skipHiddenState = false
            )
        )
        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        LaunchedEffect(snackbarController, context, viewModel, bottomSheetScaffoldState) {
            viewModel.events.collect { event ->
                when (event) {
                    is AddEditExpenseViewModel.ExpenseDetailsEvent.NavigateBackWithResult -> {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(EXPENSE_DETAILS_ACTION, event.result)
                        navController.navigateUp()
                    }

                    is AddEditExpenseViewModel.ExpenseDetailsEvent.ShowUiMessage -> {
                        snackbarController.showSnackbar(event.uiText.asString(context), event.error)
                    }

                    is AddEditExpenseViewModel.ExpenseDetailsEvent.ToggleTagInput -> {
                        if (event.show) bottomSheetScaffoldState.bottomSheetState.expand()
                        else bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }
        }

        AddEditExpenseScreenContent(
            isEditMode = isEditMode,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            amountProvider = { amount.value },
            noteProvider = { note.value },
            state = state,
            newTagProvider = { newTag.value },
            scaffoldState = bottomSheetScaffoldState,
            snackbarController = snackbarController
        )
    }
}

private const val ARG_EXPENSE_ID = "ARG_EXPENSE_ID"