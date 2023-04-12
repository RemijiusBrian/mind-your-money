package dev.ridill.mym.expenses.presentation.add_edit_expense

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.ridill.mym.core.ui.components.*
import dev.ridill.mym.core.ui.theme.*
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import kotlinx.coroutines.launch

@Composable
fun AddEditExpenseScreenContent(
    isEditMode: Boolean,
    amountProvider: () -> String,
    noteProvider: () -> String,
    state: ExpenseDetailsState,
    newTagProvider: () -> TagInput?,
    actions: AddEditExpenseActions,
    scaffoldState: BottomSheetScaffoldState,
    snackbarController: SnackbarController,
    navigateUp: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = scaffoldState.bottomSheetState.isVisible) {
        coroutineScope.launch {
            scaffoldState.bottomSheetState.hide()
        }
    }

    Box(
        modifier = Modifier
            .imePadding()
    ) {
        MYMScaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackArrowButton(onClick = navigateUp) },
                    title = {
                        Text(
                            stringResource(AddEditExpenseScreenSpec.getTitle(isEditMode))
                        )
                    },
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
            scaffoldState = scaffoldState,
            sheetContent = {
                NewTagSheetContent(
                    onTagNameChange = actions::onNewTagNameChange,
                    onTagColorSelect = actions::onNewTagColorSelect,
                    onConfirm = actions::onNewTagConfirm,
                    onDismiss = actions::onNewTagDismiss,
                    name = { newTagProvider()?.name.orEmpty() },
                    colorCode = newTagProvider()?.colorCode
                )
            },
            snackbarController = snackbarController,
            sheetPeekHeight = Dp.Zero,
            sheetSwipeEnabled = false
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
                        )
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