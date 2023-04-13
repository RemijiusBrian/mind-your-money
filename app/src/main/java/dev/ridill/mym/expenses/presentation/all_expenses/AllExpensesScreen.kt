package dev.ridill.mym.expenses.presentation.all_expenses

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.AllExpensesScreenSpec
import dev.ridill.mym.core.ui.components.*
import dev.ridill.mym.core.ui.theme.*
import dev.ridill.mym.core.util.*
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.expenses.domain.model.TagOverview
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@Composable
fun AllExpensesScreenContent(
    snackbarController: SnackbarController,
    state: AllExpensesState,
    actions: AllExpensesActions,
    navigateUp: () -> Unit
) {
    BackHandler(state.multiSelectionModeActive) {
        actions.onDismissMultiSelectionMode()
    }

    MYMScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.multiSelectionModeActive)
                            stringResource(R.string.count_selected, state.selectedExpenseIds.size)
                        else stringResource(AllExpensesScreenSpec.label)
                    )
                },
                navigationIcon = {
                    if (state.multiSelectionModeActive) {
                        IconButton(onClick = actions::onDismissMultiSelectionMode) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.content_cancel_multi_selection)
                            )
                        }
                    } else {
                        BackArrowButton(onClick = navigateUp)
                    }
                }
            )
        },
        snackbarController = snackbarController
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TagOverviews(
                tagOverviews = state.tagOverviews,
                selectedTag = state.selectedTag,
                onTagClick = actions::onTagSelect,
                modifier = Modifier
                    .height(TagOverviewHeight),
                onTagDelete = actions::onTagDelete,
                onNewTagClick = actions::onNewTagClick
            )
            AnimatedVisibility(visible = state.multiSelectionModeActive) {
                Text(
                    text = stringResource(R.string.select_tag_to_assign_to_expenses),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.PERCENT_60),
                    modifier = Modifier
                        .padding(horizontal = SpacingLarge, vertical = SpacingSmall)
                )
            }

            TotalExpenditure(state.totalExpenditureForDate)

            AnimatedVisibility(!state.multiSelectionModeActive) {
                DateSelector(
                    yearsList = state.yearsList,
                    selectedYear = state.selectedYear,
                    onYearSelect = actions::onYearSelect,
                    selectedMonth = state.selectedMonth,
                    onMonthSelect = actions::onMonthSelect
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = defaultScreenPadding(
                        bottom = SpacingListEnd
                    ),
                    verticalArrangement = Arrangement.spacedBy(SpacingMedium)
                ) {
                    if (state.multiSelectionModeActive) {
                        item(key = "MultiSelectionOptionsRow") {
                            MultiSelectionOptions(
                                selectionState = state.expenseSelectionState,
                                onSelectionStateChange = actions::onSelectionStateChange,
                                onUntagClick = actions::onUntagExpensesClick,
                                onDeleteClick = actions::onDeleteExpensesClick,
                                modifier = Modifier
                                    .animateItemPlacement()
                            )
                        }
                    }
                    items(items = state.expensesByTagForDate, key = { it.id }) { expense ->
                        ExpenseListItem(
                            note = expense.note,
                            date = expense.dateTime.format(DateUtil.Formatters.mmHyphenYyyy),
                            amount = expense.amount,
                            selected = expense.id in state.selectedExpenseIds,
                            isClickable = state.multiSelectionModeActive,
                            onClick = { actions.onExpenseSelectionToggle(expense.id) },
                            onLongClick = { actions.onExpenseLongClick(expense.id) },
                            modifier = Modifier
                                .animateItemPlacement()
                        )
                    }
                }
                // TODO: Implement no data indicator
            }
        }

        /*if (state.showTagInput) {
            TagInputDialog(
                onDismiss = actions::onNewTagDismiss,
                onConfirm = actions::onNewTagConfirm
            )
        }*/

        if (state.showTagDeletionConfirmation) {
            ConfirmationDialog(
                titleRes = R.string.dialog_delete_tag_title,
                messageRes = R.string.dialog_delete_tag_message,
                onDismiss = actions::onTagDeleteDismiss,
                onConfirm = actions::onTagDeleteConfirm,
                icon = Icons.Outlined.DeleteForever
            )
        }

        if (state.showExpenseDeleteConfirmation) {
            ConfirmationDialog(
                titleRes = R.string.dialog_delete_selected_expense_title,
                messageRes = R.string.dialog_delete_selected_expense_message,
                onDismiss = actions::onDeleteExpensesDismissed,
                onConfirm = actions::onDeleteExpensesConfirmed,
                icon = Icons.Rounded.DeleteForever
            )
        }
    }
}

@Composable
private fun TagOverviews(
    tagOverviews: List<TagOverview>,
    selectedTag: String?,
    onTagClick: (String) -> Unit,
    onTagDelete: (String) -> Unit,
    onNewTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = defaultScreenPadding(
            top = Dp.Zero,
            bottom = Dp.Zero,
            end = SpacingListEnd,
            start = SpacingLarge
        ),
        horizontalArrangement = Arrangement.spacedBy(SpacingSmall)
    ) {
        items(items = tagOverviews, key = { it.tag }) { overview ->
            TagOverviewCard(
                name = overview.tag,
                color = overview.color,
                percentOfTotal = overview.percentOfTotal,
                amount = Formatter.currency(overview.amount),
                expanded = overview.tag == selectedTag,
                onClick = { onTagClick(overview.tag) },
                onDeleteClick = { onTagDelete(overview.tag) },
                modifier = Modifier
                    .animateItemPlacement()
                    .fillParentMaxHeight()
            )
        }

        item(key = "NewTag") {
            Surface(
                tonalElevation = ElevationLevel1,
                shape = MaterialTheme.shapes.large,
                onClick = onNewTagClick,
                modifier = Modifier
//                    .animateItemPlacement()
            ) {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .width(TagOverviewCardBaseWidth),
                    contentAlignment = Alignment.Center
                ) {
                    FilledTonalIconButton(
                        onClick = onNewTagClick,
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.content_new_tag)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagOverviewCard(
    name: String,
    color: Color,
    percentOfTotal: Float,
    amount: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = color.onColor()
) {
    val transition = updateTransition(targetState = expanded, label = "tagSelection")
    val textPadding by transition.animateDp(
        label = "textPadding",
        targetValueByState = { if (it) SpacingMedium else SpacingSmall }
    )
    val animatedPercent by animateFloatAsState(percentOfTotal)

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(TagOverviewCardBaseWidth * (if (expanded) 2f else 1f))
            .then(modifier),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .weight(Float.One)
                    .padding(horizontal = SpacingMedium, vertical = SpacingSmall)
            ) {
                Text(
                    text = name,
                    style = if (expanded) MaterialTheme.typography.titleLarge
                    else MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(top = textPadding),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(SpacingSmall))
                Text(
                    text = stringResource(
                        R.string.percent_of_total,
                        Formatter.percentage(animatedPercent)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(SpacingSmall))
                if (expanded) {
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            if (expanded) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DoNotDisturbOn,
                        contentDescription = stringResource(R.string.content_delete),
                        tint = contentColor.copy(alpha = ContentAlpha.PERCENT_60)
                    )
                }
            }
            VerticalProgressIndicator(
                progress = animatedPercent,
                width = OverviewProgressBarBaseWidth * (if (expanded) 2f else 1f),
                modifier = Modifier
                    .fillMaxHeight(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = ContentAlpha.PERCENT_32),
                indicatorColor = color.copy(alpha = ContentAlpha.PERCENT_60)
            )
        }
    }
}

private val TagOverviewHeight = 160.dp
private val TagOverviewCardBaseWidth = 120.dp
private val OverviewProgressBarBaseWidth = 12.dp

@Composable
private fun TotalExpenditure(
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingMedium)
            .padding(top = SpacingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.total_spent),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth(0.60f)
        )
        AnimatedContent(
            targetState = amount,
            transitionSpec = { verticalSpinner() }
        ) { value ->
            Text(
                text = Formatter.currency(value),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DateSelector(
    yearsList: List<String>,
    selectedYear: String?,
    onYearSelect: (String) -> Unit,
    selectedMonth: Int,
    onMonthSelect: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        YearSelector(
            yearsList = yearsList,
            selectedYear = selectedYear,
            onYearSelect = onYearSelect
        )
        val monthsList = remember { Month.values().toList() }
        MonthSelector(
            monthsList = monthsList,
            selectedMonth = selectedMonth,
            onMonthSelect = onMonthSelect
        )
    }
}

@Composable
private fun YearSelector(
    yearsList: List<String>,
    selectedYear: String?,
    onYearSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showList by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(targetValue = if (showList) 180f else 0f)
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = SpacingMedium)
                .clip(MaterialTheme.shapes.small)
                .clickable(role = Role.DropdownList) { showList = !showList }
                .padding(SpacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Crossfade(targetState = selectedYear) { year ->
                Text(
                    text = year.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(Modifier.width(SpacingXSmall))
            Icon(
                imageVector = Icons.Default.ArrowLeft,
                contentDescription = stringResource(R.string.content_toggle_years_list),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = iconRotation
                    }
            )
        }
        AnimatedVisibility(
            visible = showList,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut()
        ) {
            LazyRow(
                contentPadding = PaddingValues(
                    start = SpacingMedium,
                    end = SpacingListEnd
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(items = yearsList, key = { it }) { year ->
                    val selected = year == selectedYear
                    val alpha by animateFloatAsState(
                        targetValue = if (selected) Float.One else ContentAlpha.PERCENT_32
                    )
                    Surface(
                        onClick = {
                            showList = false
                            onYearSelect(year)
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .animateItemPlacement()
                    ) {
                        Text(
                            text = year,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(SpacingXSmall)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    monthsList: List<Month>,
    selectedMonth: Int,
    onMonthSelect: (Month) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = SpacingMedium,
            end = SpacingListEnd
        )
    ) {
        items(monthsList, key = { it.value }) { month ->
            MonthItem(
                month = month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                selected = month.value == selectedMonth,
                onClick = { onMonthSelect(month) }
            )
        }
    }
}

@Composable
private fun MonthItem(
    month: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = selected, label = "monthSelection")
    val alpha by transition.animateFloat(
        label = "monthAlpha",
        targetValueByState = { if (it) Float.One else ContentAlpha.PERCENT_32 }
    )
    val elevation by transition.animateDp(
        label = "monthElevation",
        targetValueByState = { if (it) ElevationLevel1 else ElevationLevel0 }
    )

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        tonalElevation = elevation,
        color = Color.Transparent,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 40.dp, minHeight = 32.dp)
                .padding(horizontal = SpacingXSmall),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = month,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(SpacingXSmall)
            )
        }
    }
}

@Composable
private fun ExpenseListItem(
    note: String,
    date: String,
    amount: String,
    selected: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SelectableExpenseCard(
        onClick = { if (isClickable) onClick() },
        onLongClick = onLongClick,
        note = note,
        date = date,
        amount = amount,
        selected = selected,
        modifier = modifier
    )
    /*Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = {
                    if (isClickable) onClick()
                },
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else Color.Transparent
        )
    ) {
        *//*BaseExpenseCardLayout(
            note = note,
            date = date,
            amount = amount,
            tag = null
        )*//*
    }*/
}

@Composable
private fun MultiSelectionOptions(
    selectionState: ToggleableState,
    onSelectionStateChange: (ToggleableState) -> Unit,
    onDeleteClick: () -> Unit,
    onUntagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = stringResource(R.string.content_delete)
            )
        }
        IconButton(onClick = onUntagClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_de_tag),
                contentDescription = stringResource(R.string.content_de_tag)
            )
        }
        TriStateCheckbox(
            state = selectionState,
            onClick = { onSelectionStateChange(selectionState) })
    }
}