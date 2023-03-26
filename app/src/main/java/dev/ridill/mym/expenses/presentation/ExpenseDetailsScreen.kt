package dev.ridill.mym.expenses.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.components.*
import dev.ridill.mym.core.ui.theme.*
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.core.util.onColor
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagColors
import dev.ridill.mym.expenses.domain.model.TagInput
import kotlinx.coroutines.launch

@Composable
fun ExpenseDetailsScreen(
    isEditMode: Boolean,
    viewModel: ExpenseDetailsViewModel,
    navigateUp: () -> Unit,
    navigateBackWithResult: (String) -> Unit
) {
    val amount by viewModel.amount.collectAsStateWithLifecycle("")
    val note by viewModel.note.collectAsStateWithLifecycle("")
    val state by viewModel.state.collectAsStateWithLifecycle()
    val newTag = viewModel.newTag.collectAsStateWithLifecycle()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val snackbarController = rememberSnackbarController()
    val context = LocalContext.current

    LaunchedEffect(snackbarController, context, viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ExpenseDetailsViewModel.ExpenseDetailsEvent.NavigateBackWithResult -> {
                    navigateBackWithResult(event.result)
                }
                is ExpenseDetailsViewModel.ExpenseDetailsEvent.ShowUiMessage -> {
                    snackbarController.showSnackbar(event.uiText.asString(context), event.error)
                }
                is ExpenseDetailsViewModel.ExpenseDetailsEvent.ToggleTagInput -> {
                    if (event.show) {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    } else {
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }
        }
    }

    ScreenContent(
        scaffoldState = bottomSheetScaffoldState,
        snackbarController = snackbarController,
        isEditMode = isEditMode,
        amount = amount,
        onAmountChange = viewModel::onAmountChange,
        note = note,
        onNoteChange = viewModel::onNoteChange,
        onDeleteClick = viewModel::onDeleteClick,
        state = state,
        onTagSelect = viewModel::onTagSelect,
        onNewTagClick = viewModel::onNewTagClick,
        newTag = { newTag.value },
        onTagNameChange = viewModel::onNewTagNameChange,
        onTagColorSelect = viewModel::onNewTagColorSelect,
        onNewTagDismiss = viewModel::onNewTagDismiss,
        onNewTagConfirm = viewModel::onNewTagConfirm,
        onSave = viewModel::onSaveClick,
        navigateUp = navigateUp
    )
}

@Composable
private fun ScreenContent(
    scaffoldState: BottomSheetScaffoldState,
    snackbarController: SnackbarController,
    isEditMode: Boolean,
    amount: String,
    onAmountChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    state: ExpenseDetailsState,
    onDeleteClick: () -> Unit,
    onTagSelect: (Tag) -> Unit,
    onNewTagClick: () -> Unit,
    newTag: () -> TagInput,
    onTagNameChange: (String) -> Unit,
    onTagColorSelect: (Color) -> Unit,
    onNewTagDismiss: () -> Unit,
    onNewTagConfirm: () -> Unit,
    onSave: () -> Unit,
    navigateUp: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = scaffoldState.bottomSheetState.isVisible) {
        coroutineScope.launch {
            scaffoldState.bottomSheetState.hide()
        }
    }

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
                        IconButton(onClick = onDeleteClick) {
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
            TagInputSheet(
                newTag = newTag,
                onTagNameChange = onTagNameChange,
                onTagColorSelect = onTagColorSelect,
                onConfirm = onNewTagConfirm,
                onDismiss = onNewTagDismiss
            )
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
                TextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = { Text(Formatter.defaultCurrencySymbol()) },
                    placeholder = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier
                        .defaultMinSize(minWidth = InputFieldMinWidth)
                        .widthIn(min = InputFieldMinWidth, max = InputFieldMaxWidth),
                    textStyle = MaterialTheme.typography.headlineMedium
                        .copy(textAlign = TextAlign.Center),
                    singleLine = true
                )

                TextField(
                    value = note,
                    onValueChange = onNoteChange,
                    textStyle = TextStyle.Default.copy(
                        textAlign = TextAlign.Center
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .defaultMinSize(minWidth = InputFieldMinWidth)
                        .widthIn(max = InputFieldMaxWidth),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                            .copy(alpha = ContentAlpha.PERCENT_32)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSave() }
                    ),
                    placeholder = { Text(stringResource(R.string.add_note)) },
                    maxLines = 2
                )

                Tags(
                    tagsList = state.tagsList,
                    selectedTagName = state.selectedTagName,
                    onTagClick = onTagSelect,
                    onNewTagClick = onNewTagClick
                )
            }

            FloatingActionButton(
                onClick = onSave,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = stringResource(R.string.content_save)
                )
            }
        }
    }
}

private val InputFieldMinWidth = 80.dp
private val InputFieldMaxWidth = 240.dp

@OptIn(ExperimentalLayoutApi::class)
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

@Composable
private fun TagInputSheet(
    newTag: () -> TagInput,
    onTagNameChange: (String) -> Unit,
    onTagColorSelect: (Color) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(
                vertical = SpacingSmall,
                horizontal = SpacingLarge
            ),
        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TitleText(labelRes = R.string.new_tag)
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.content_dismiss)
                )
            }
        }
        OutlinedTextField(
            value = newTag().name,
            onValueChange = onTagNameChange,
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            label = { Text(stringResource(R.string.name)) }
        )
        Divider()
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingSmall)
        ) {
            items(items = TagColors) { color ->
                ColorSelector(
                    color = color,
                    selected = newTag().colorCode == color.toArgb(),
                    onClick = { onTagColorSelect(color) }
                )
            }
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.action_confirm))
        }
    }
}


@Composable
private fun ColorSelector(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = color.onColor()
) {
    val borderWidthFraction by animateFloatAsState(targetValue = if (selected) 0.30f else 0.10f)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(ColorSelectorSize)
            .clickable { onClick() }
            .drawBehind {
                drawCircle(color)
                drawCircle(
                    color = borderColor,
                    style = Stroke(ColorSelectorSize.toPx() * borderWidthFraction)
                )
            }
    )
}

private val ColorSelectorSize = 32.dp

@Preview(showBackground = true)
@Composable
private fun PreviewScreenContent() {
    MYMTheme {
        ScreenContent(
            snackbarController = rememberSnackbarController(),
            isEditMode = true,
            amount = "",
            onAmountChange = {},
            note = "",
            onNoteChange = {},
            onDeleteClick = {},
            navigateUp = {},
            onSave = {},
            onTagSelect = {},
            onNewTagClick = {},
            onNewTagDismiss = {},
            onNewTagConfirm = {},
            state = ExpenseDetailsState(),
            scaffoldState = rememberBottomSheetScaffoldState(),
            newTag = { TagInput.INITIAL },
            onTagNameChange = {},
            onTagColorSelect = {}
        )
    }
}