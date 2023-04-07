package dev.ridill.mym.expenses.presentation.expenseDetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Save
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
import dev.ridill.mym.core.ui.components.*
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.SpacingLargeTop
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.ui.theme.defaultScreenPadding
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import kotlinx.coroutines.launch

@Composable
fun ExpenseDetailsScreenContent(
    isEditMode: Boolean,
    amountProvider: () -> String,
    noteProvider: () -> String,
    state: ExpenseDetailsState,
    newTagProvider: () -> TagInput?,
    actions: ExpenseDetailsActions,
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

    Box(modifier = Modifier.imePadding()) {
        MYMScaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackArrowButton(onClick = navigateUp) },
                    title = {
                        Text(
                            stringResource(
                                id = if (isEditMode) R.string.edit_expense
                                else R.string.add_expense
                            )
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
                newTagProvider()?.let { input ->
                    NewTagSheet(
                        onTagNameChange = actions::onNewTagNameChange,
                        onTagColorSelect = actions::onNewTagColorSelect,
                        onConfirm = actions::onNewTagConfirm,
                        onDismiss = actions::onNewTagDismiss,
                        name = { input.name },
                        colorCode = input.colorCode
                    )
                }
            },
            snackbarController = snackbarController,
            sheetPeekHeight = Dp.Zero,
            sheetSwipeEnabled = false
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(defaultScreenPadding(top = SpacingLargeTop))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(Float.One)
                        .verticalScroll(rememberScrollState()),
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

//                    val containerColor = MaterialTheme.colorScheme.surfaceVariant
//                        .copy(alpha = ContentAlpha.PERCENT_32)
                    TextField(
                        value = noteProvider(),
                        onValueChange = actions::onNoteChange,
                        textStyle = TextStyle.Default.copy(
                            textAlign = TextAlign.Center
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .defaultMinSize(minWidth = InputFieldMinWidth)
                            .widthIn(max = InputFieldMaxWidth),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                .copy(alpha = ContentAlpha.PERCENT_32),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                .copy(alpha = ContentAlpha.PERCENT_32),
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                .copy(alpha = ContentAlpha.PERCENT_32),
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
                            onDone = { actions.onSave() }
                        ),
                        placeholder = { Text(stringResource(R.string.add_note)) }
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
                        .align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
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