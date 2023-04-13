package dev.ridill.mym.expenses.presentation.expense_management

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.ExpenseManagementScreenSpec
import dev.ridill.mym.core.ui.components.*
import dev.ridill.mym.core.ui.theme.*
import dev.ridill.mym.core.util.*
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagOverview
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@Composable
fun ExpenseManagementScreenContent(
    snackbarController: SnackbarController,
    state: ExpenseManagementState,
    actions: ExpenseManagementActions,
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
                        text = if (state.multiSelectionModeActive) stringResource(
                            R.string.count_selected,
                            state.selectedExpenseIds.size
                        )
                        else stringResource(ExpenseManagementScreenSpec.label)
                    )
                },
                navigationIcon = {
                    if (state.multiSelectionModeActive) {
                        IconButton(onClick = actions::onDismissMultiSelectionMode) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
//                                stringResource(R.string.content_description_cancel_multi_selection_mode)
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
                    items(items = state.expenses, key = { it.id }) { expense ->
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

        /*if (state.showExpenseDeleteConfirmation) {
            SimpleConfirmationDialog(
                title = R.string.dialog_delete_selected_expense_title,
                text = R.string.dialog_delete_selected_expense_message,
                onDismiss = actions::onDeleteExpensesDismissed,
                onConfirm = actions::onDeleteExpensesConfirmed,
                icon = Icons.Rounded.DeleteForever
            )
        }*/
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
                isUntagged = overview.tag == Tag.Untagged.name,
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
                            contentDescription = null
//                            stringResource(R.string.content_description_create_new_tag)
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
    isUntagged: Boolean,
    modifier: Modifier = Modifier,
    contentColor: Color = color.onColor()
) {
    val transition = updateTransition(targetState = expanded, label = "tagSelection")
    val width by transition.animateDp(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "cardWidth",
        targetValueByState = { if (it) TagOverviewCardBaseWidth * 2f else TagOverviewCardBaseWidth }
    )
    val textSize by transition.animateFloat(
        label = "textSize",
        targetValueByState = { if (it) TAG_TEXT_SIZE_EXPANDED else TAG_TEXT_SIZE_SMALL }
    )
    val cornerRadius by transition.animateDp(
        label = "cardCorner",
        targetValueByState = { if (it) CornerRadiusLarge else CornerRadiusMedium }
    )
    val textPadding by transition.animateDp(
        label = "textPadding",
        targetValueByState = { if (it) SpacingMedium else SpacingSmall }
    )
    val progressIndicatorWidth by transition.animateDp(
        label = "progressIndicatorWidth",
        targetValueByState = { if (it) 24.dp else 12.dp }
    )
    val animatedPercent by animateFloatAsState(percentOfTotal)

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(width)
            .then(modifier),
        shape = RoundedCornerShape(cornerRadius),
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
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                    modifier = Modifier
                        .padding(top = textPadding)
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
            if (!isUntagged && expanded) {
//                AnimatedVisibility(
//                    visible = expanded
//                ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DoNotDisturbOn,
                        contentDescription = null
                        /*stringResource(
                            R.string.content_description_delete_tag
                        )*/,
                        tint = contentColor.copy(alpha = ContentAlpha.PERCENT_16)
                    )
                }
//                }
            }
            VerticalProgressIndicator(
                progress = animatedPercent,
                width = progressIndicatorWidth,
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
private const val TAG_TEXT_SIZE_SMALL = 16f
private const val TAG_TEXT_SIZE_EXPANDED = 20f

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
    var expanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    Column(
        modifier = modifier
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = SpacingMedium)
                .clip(MaterialTheme.shapes.small)
                .clickable { expanded = !expanded }
                .padding(SpacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedYear.orEmpty(),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.width(SpacingXSmall))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
//                stringResource(R.string.content_description_toggle_dropdown),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = iconRotation
                    }
            )
        }
        if (expanded) {
            LazyRow(
                contentPadding = PaddingValues(
                    start = SpacingMedium,
                    end = SpacingListEnd
                )
            ) {
                items(yearsList, key = { it }) { year ->
                    val selected = year == selectedYear
                    val alpha by animateFloatAsState(
                        targetValue = if (selected) Float.One else ContentAlpha.PERCENT_32
                    )
                    Surface(
                        onClick = { onYearSelect(year) },
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
    Card(
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
        /*BaseExpenseCardLayout(
            note = note,
            date = date,
            amount = amount,
            tag = null
        )*/
    }
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
                contentDescription = null
//                stringResource(R.string.content_description_delete_expense)
            )
        }
        /*IconButton(onClick = onUntagClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_untag),
                contentDescription = stringResource(R.string.content_description_untag_expenses)
            )
        }*/
        TriStateCheckbox(
            state = selectionState,
            onClick = { onSelectionStateChange(selectionState) })
    }
}