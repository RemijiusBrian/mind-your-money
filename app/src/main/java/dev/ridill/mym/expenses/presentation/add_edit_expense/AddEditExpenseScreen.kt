package dev.ridill.mym.expenses.presentation.add_edit_expense

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.AddEditExpenseScreenSpec
import dev.ridill.mym.core.ui.components.BackArrowButton
import dev.ridill.mym.core.ui.components.ConfirmationDialog
import dev.ridill.mym.core.ui.components.LabelText
import dev.ridill.mym.core.ui.components.MYMBottomSheetScaffold
import dev.ridill.mym.core.ui.components.MinWidthTextField
import dev.ridill.mym.core.ui.components.NewTagChip
import dev.ridill.mym.core.ui.components.NewTagSheetContent
import dev.ridill.mym.core.ui.components.SnackbarController
import dev.ridill.mym.core.ui.components.TagChip
import dev.ridill.mym.core.ui.components.VerticalSpacer
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.FabSpacing
import dev.ridill.mym.core.ui.theme.SpacingLargeTop
import dev.ridill.mym.core.ui.theme.SpacingListEnd
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.ui.theme.defaultScreenPadding
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditExpenseScreenContent(
    isEditMode: Boolean,
    amountProvider: () -> String,
    noteProvider: () -> String,
    state: AddEditExpenseState,
    newTagProvider: () -> TagInput?,
    actions: AddEditExpenseActions,
    scaffoldState: BottomSheetScaffoldState,
    snackbarController: SnackbarController,
    navigateUp: () -> Unit
) {
    BackHandler(
        enabled = scaffoldState.bottomSheetState.isVisible,
        onBack = actions::dismissNewTagInput
    )

    val amountFocusRequester = remember { FocusRequester() }
    val tagInputFocusRequester = remember { FocusRequester() }

    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow { scaffoldState.bottomSheetState.currentValue }
            .collectLatest { sheetValue ->
                if (sheetValue == SheetValue.Expanded) {
                    tagInputFocusRequester.requestFocus()
                }
            }
    }

    LaunchedEffect(Unit) {
        amountFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .imePadding()
    ) {
        MYMBottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                NewTagSheetContent(
                    onTagNameChange = actions::onNewTagNameChange,
                    onTagColorSelect = actions::onNewTagColorSelect,
                    onConfirm = actions::onNewTagConfirm,
                    onDismiss = actions::dismissNewTagInput,
                    name = { newTagProvider()?.name.orEmpty() },
                    colorCode = newTagProvider()?.colorCode,
                    inputFocusRequester = tagInputFocusRequester
                )
            },
            sheetPeekHeight = Dp.Zero,
            sheetDragHandle = null,
            sheetSwipeEnabled = false,
            topBar = {
                TopAppBar(
                    navigationIcon = { BackArrowButton(onClick = navigateUp) },
                    title = { Text(stringResource(AddEditExpenseScreenSpec.getTitle(isEditMode))) },
                    actions = {
                        if (isEditMode) {
                            IconButton(onClick = actions::onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.content_delete)
                                )
                            }
                        }
                    }
                )
            },
            snackbarController = snackbarController
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            defaultScreenPadding(
                                top = SpacingLargeTop,
                                bottom = SpacingListEnd
                            )
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SpacingSmall)
                ) {
                    MinWidthTextField(
                        valueProvider = amountProvider,
                        onValueChange = actions::onAmountChange,
                        textStyle = MaterialTheme.typography.displayMedium,
                        leadingIcon = {
                            Text(
                                text = Formatter.defaultCurrencySymbol(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        placeholder = Int.Zero.toString(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier
                            .focusRequester(amountFocusRequester)
                    )

                    NoteEntryField(
                        noteProvider = noteProvider,
                        onNoteChange = actions::onNoteChange,
                        onDone = actions::onSave
                    )

                    Tags(
                        tagsList = state.tagsList,
                        selectedTagName = state.selectedTagName,
                        onTagClick = actions::onTagSelect,
                        onNewTagClick = actions::onNewTagClick
                    )
                }

                FloatingActionButton(
                    onClick = actions::onSave,
                    modifier = Modifier
                        .padding(FabSpacing)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(R.string.content_save)
                    )
                }
            }

            if (state.showDeleteConfirmation) {
                ConfirmationDialog(
                    titleRes = R.string.dialog_delete_expense_title,
                    messageRes = R.string.dialog_delete_expense_message,
                    onDismiss = actions::onDeleteDismiss,
                    onConfirm = actions::onDeleteConfirm
                )
            }
        }
    }
}

@Composable
private fun NoteEntryField(
    noteProvider: () -> String,
    onNoteChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
        .copy(alpha = ContentAlpha.PERCENT_32)
    TextField(
        value = noteProvider(),
        onValueChange = onNoteChange,
        textStyle = TextStyle.Default.copy(
            textAlign = TextAlign.Center
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .defaultMinSize(minWidth = InputFieldMinWidth)
            .widthIn(max = InputFieldMaxWidth),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        placeholder = { Text(stringResource(R.string.add_note)) }
    )
}

private val InputFieldMinWidth = 80.dp
private val InputFieldMaxWidth = 240.dp

@Composable
private fun Tags(
    tagsList: List<Tag>,
    selectedTagName: String?,
    onTagClick: (Tag) -> Unit,
    onNewTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LabelText(labelRes = R.string.tags)
        VerticalSpacer(spacing = SpacingSmall)
        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SpacingSmall)
        ) {
            tagsList.forEach { tag ->
                TagChip(
                    name = tag.name,
                    color = tag.color,
                    selected = tag.name == selectedTagName,
                    onClick = { onTagClick(tag) }
                )
            }

            NewTagChip(onClick = onNewTagClick)
        }
    }
}