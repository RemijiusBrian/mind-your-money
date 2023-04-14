package dev.ridill.mym.dashboard.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.BottomBarSpec
import dev.ridill.mym.core.navigation.screenSpecs.DashboardScreenSpec
import dev.ridill.mym.core.ui.components.*
import dev.ridill.mym.core.ui.theme.*
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navigateToExpenseDetails: (Long?) -> Unit,
    navigateToBottomBarSpec: (BottomBarSpec) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarController = rememberSnackbarController()
    val context = LocalContext.current

    LaunchedEffect(snackbarController, context, viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardViewModel.DashboardEvents.ShowUiMessage -> {
                    snackbarController.showSnackbar(event.uiText.asString(context))
                }
            }
        }
    }

    ScreenContent(
        snackbarController = snackbarController,
        state = state,
        onAddFabClick = { navigateToExpenseDetails(null) },
        onExpenseClick = navigateToExpenseDetails,
        onBottomBarActionClick = navigateToBottomBarSpec
    )
}

@Composable
private fun ScreenContent(
    snackbarController: SnackbarController,
    state: DashboardState,
    onAddFabClick: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    onBottomBarActionClick: (BottomBarSpec) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val showScrollUpButton by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 3 }
    }
    val coroutineScope = rememberCoroutineScope()

    MYMScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(DashboardScreenSpec.label)) }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    BottomBarSpec.bottomBarDestinations.forEach { spec ->
                        IconButton(onClick = { onBottomBarActionClick(spec) }) {
                            Icon(
                                imageVector = spec.icon,
                                contentDescription = stringResource(spec.label)
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = onAddFabClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.content_add_new_expense)
                        )
                    }
                },
                tonalElevation = ElevationLevel1
            )
        },
        snackbarController = snackbarController
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(defaultScreenPadding())
        ) {
            ExpenditureOverview(
                isLimitSet = state.isMonthlyLimitSet,
                monthlyLimit = state.monthlyLimit,
                amountSpent = state.expenditure,
                balance = state.balanceFromLimit,
                balancePercentOfLimit = state.balancePercent
            )
            VerticalSpacer(spacing = SpacingMedium)
            LabelText(labelRes = R.string.expenses)
            VerticalSpacer(spacing = SpacingMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(Float.One),
                contentAlignment = Alignment.Center
            ) {
                if (state.expenses.isEmpty()) {
                    ListEmptyIndicator(R.string.expense_list_empty)
                }
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(SpacingSmall),
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    items(items = state.expenses, key = { it.id }) { expense ->
                        ExpenseCard(
                            onClick = { onExpenseClick(expense.id) },
                            note = expense.note,
                            date = expense.dateFormatted,
                            amount = expense.amountFormatted,
                            modifier = Modifier
                                .animateItemPlacement(),
                            tag = expense.tag
                        )
                    }
                }

                this@Column.AnimatedVisibility(
                    visible = showScrollUpButton,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    SmallFloatingActionButton(onClick = {
                        coroutineScope.launch {
                            if (lazyListState.isScrollInProgress) {
                                lazyListState.scrollToItem(Int.Zero)
                            } else {
                                lazyListState.animateScrollToItem(Int.Zero)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = stringResource(R.string.content_scroll_up)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenditureOverview(
    isLimitSet: Boolean,
    monthlyLimit: Long,
    amountSpent: Double,
    balance: Double,
    balancePercentOfLimit: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = OverviewMaxHeight)
    ) {
        if (isLimitSet) {
            OverviewStat(
                title = R.string.your_limit,
                modifier = Modifier
                    .weight(Float.One),
                titleStyle = MaterialTheme.typography.headlineSmall,
                valueStyle = MaterialTheme.typography.headlineMedium
                    .copy(fontWeight = FontWeight.SemiBold)
            ) {
                VerticalNumberSpinner(targetState = monthlyLimit) {
                    Text(Formatter.currency(it))
                }
            }
            HorizontalSpacer(spacing = SpacingSmall)
        }
        Column(
            modifier = Modifier
                .weight(Float.One)
        ) {
            OverviewStat(
                title = R.string.youve_spent,
                modifier = Modifier
                    .weight(Float.One),
                titleStyle = if (!isLimitSet) MaterialTheme.typography.headlineSmall
                else MaterialTheme.typography.titleMedium,
                valueStyle = if (!isLimitSet) MaterialTheme.typography.headlineMedium
                else MaterialTheme.typography.titleLarge
            ) {
                VerticalNumberSpinner(targetState = amountSpent) {
                    Text(Formatter.currency(it))
                }
            }
            if (isLimitSet) {
                VerticalSpacer(SpacingSmall)
                BalanceCard(
                    balanceAmount = balance,
                    balancePercentOfLimit = balancePercentOfLimit,
                    modifier = Modifier
                        .weight(Float.One)
                )
            }
        }
    }
}

private val OverviewMaxHeight = 160.dp

@Composable
private fun OverviewStat(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    valueStyle: TextStyle = MaterialTheme.typography.titleLarge
        .copy(fontWeight = FontWeight.SemiBold),
    colors: CardColors = CardDefaults.cardColors(),
    value: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        colors = colors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingSmall),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(title),
                style = titleStyle
            )
            Spacer(Modifier.height(SpacingXSmall))
            ProvideTextStyle(value = valueStyle) {
                value()
            }
        }
    }
}

@Composable
private fun BalanceCard(
    balanceAmount: Double,
    balancePercentOfLimit: Float,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadiusMedium
) {
    val balanceSafeColor = MaterialTheme.colorScheme.secondary
    val onBalanceSafeColor = contentColorFor(balanceSafeColor)
    val balanceErrorColor = MaterialTheme.colorScheme.errorContainer
    val onBalanceErrorColor = contentColorFor(balanceErrorColor)
    val colorAnimatable = remember {
        TargetBasedAnimation(
            animationSpec = tween(),
            typeConverter = Color.VectorConverter(ColorSpaces.Srgb),
            initialValue = balanceErrorColor,
            targetValue = balanceSafeColor,
            initialVelocity = balanceErrorColor
        )
    }
    val onColorAnimatable = remember {
        TargetBasedAnimation(
            animationSpec = tween(),
            typeConverter = Color.VectorConverter(ColorSpaces.Srgb),
            initialValue = onBalanceErrorColor,
            targetValue = onBalanceSafeColor,
            initialVelocity = onBalanceErrorColor
        )
    }

    OverviewStat(
        title = R.string.balance,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = onColorAnimatable.getValueFromNanos(
                (onColorAnimatable.durationNanos * balancePercentOfLimit).roundToLong()
            )
        ),
        modifier = modifier.drawBehind {
            val color = colorAnimatable.getValueFromNanos(
                (colorAnimatable.durationNanos * balancePercentOfLimit).roundToLong()
            )

            drawRoundRect(
                color = color.copy(alpha = ContentAlpha.PERCENT_16),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )

            drawRoundRect(
                color = color,
                size = size.copy(
                    width = size.width * balancePercentOfLimit
                ),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        }
    ) {
        VerticalNumberSpinner(targetState = balanceAmount) {
            Text(Formatter.currency(it))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScreenContent() {
    MYMTheme {
        ScreenContent(
            state = DashboardState(),
            onAddFabClick = {},
            onExpenseClick = {},
            onBottomBarActionClick = {},
            snackbarController = rememberSnackbarController()
        )
    }
}