package dev.ridill.mym.dashboard.presentation

import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.ARG_QUICK_ACTION_LIMIT_UPDATE
import dev.ridill.mym.core.navigation.screenSpecs.BottomBarSpec
import dev.ridill.mym.core.navigation.screenSpecs.DashboardScreenSpec
import dev.ridill.mym.core.ui.components.ExpenseCard
import dev.ridill.mym.core.ui.components.HorizontalSpacer
import dev.ridill.mym.core.ui.components.LabelText
import dev.ridill.mym.core.ui.components.MYMScaffold
import dev.ridill.mym.core.ui.components.SnackbarController
import dev.ridill.mym.core.ui.components.VerticalNumberSpinner
import dev.ridill.mym.core.ui.components.VerticalSpacer
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.CornerRadiusMedium
import dev.ridill.mym.core.ui.theme.ElevationLevel1
import dev.ridill.mym.core.ui.theme.MYMTheme
import dev.ridill.mym.core.ui.theme.SpacingLarge
import dev.ridill.mym.core.ui.theme.SpacingListEnd
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.ui.theme.SpacingXSmall
import dev.ridill.mym.core.ui.theme.defaultScreenPadding
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@Composable
fun DashboardScreenContent(
    snackbarController: SnackbarController,
    state: DashboardState,
    onAddFabClick: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    onBottomBarActionClick: (BottomBarSpec) -> Unit,
    navigateToSettingsWithAction: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val showScrollUpButton by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex >= 3 }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .matchParentSize(),
                contentPadding = defaultScreenPadding(
                    bottom = SpacingListEnd
                ),
                verticalArrangement = Arrangement.spacedBy(SpacingMedium),
                state = lazyListState
            ) {
                item(key = "Expenditure Overview") {
                    ExpenditureOverview(
                        monthlyLimit = state.monthlyLimit,
                        amountSpent = state.expenditure,
                        balance = state.balanceFromLimit,
                        balancePercentOfLimit = state.balancePercent,
                        showBalanceLowWarning = state.showBalanceLowWarning,
                        onLimitCardClick = {
                            navigateToSettingsWithAction(ARG_QUICK_ACTION_LIMIT_UPDATE)
                        },
                        modifier = Modifier
                            .animateItemPlacement()
                    )
                }

                item(key = "Expense Label") {
                    LabelText(
                        labelRes = R.string.expenses,
                        modifier = Modifier
                            .animateItemPlacement()
                    )
                }

                items(items = state.expenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        onClick = { onExpenseClick(expense.id) },
                        note = expense.note,
                        date = expense.dateFormatted,
                        amount = expense.amountFormatted,
                        tag = expense.tag,
                        modifier = Modifier
                            .animateItemPlacement()
                    )
                }
            }

            AnimatedVisibility(
                visible = showScrollUpButton,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .padding(SpacingLarge)
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

@Composable
private fun ExpenditureOverview(
    monthlyLimit: Long,
    amountSpent: Double,
    balance: Double,
    balancePercentOfLimit: Float,
    showBalanceLowWarning: Boolean,
    onLimitCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = OverviewMaxHeight)
    ) {
        ClickableOverviewStat(
            title = R.string.your_limit,
            onClick = onLimitCardClick,
            modifier = Modifier
                .weight(Float.One),
            titleStyle = MaterialTheme.typography.headlineSmall,
            valueStyle = MaterialTheme.typography.headlineMedium
                .copy(fontWeight = FontWeight.SemiBold)
        ) {
            Column {
                VerticalNumberSpinner(targetState = monthlyLimit) {
                    Text(Formatter.currency(it))
                }
                if (monthlyLimit <= Long.Zero) {
                    Text(
                        text = stringResource(R.string.click_to_set_limit),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        HorizontalSpacer(spacing = SpacingSmall)
        Column(
            modifier = Modifier
                .weight(Float.One)
        ) {
            OverviewStat(
                title = R.string.youve_spent,
                modifier = Modifier
                    .weight(Float.One),
                titleStyle = MaterialTheme.typography.titleMedium,
                valueStyle = MaterialTheme.typography.titleLarge
            ) {
                VerticalNumberSpinner(targetState = amountSpent) {
                    Text(Formatter.currency(it))
                }
            }
            VerticalSpacer(SpacingSmall)
            BalanceCard(
                balanceAmount = balance,
                balancePercentOfLimit = balancePercentOfLimit,
                modifier = Modifier
                    .weight(Float.One),
                showBalanceLowWarning = showBalanceLowWarning
            )
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
private fun ClickableOverviewStat(
    @StringRes title: Int,
    onClick: () -> Unit,
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
        colors = colors,
        onClick = onClick
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
    @FloatRange(0.0, 1.0) balancePercentOfLimit: Float,
    showBalanceLowWarning: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadiusMedium
) {
    val balanceSafeColor = MaterialTheme.colorScheme.secondary
    val onBalanceSafeColor = contentColorFor(balanceSafeColor)
    val balanceErrorColor = MaterialTheme.colorScheme.errorContainer
    val onBalanceErrorColor = contentColorFor(balanceErrorColor)
    val containerColorAnimatable = remember(balanceSafeColor, balanceErrorColor) {
        TargetBasedAnimation(
            animationSpec = tween(),
            typeConverter = Color.VectorConverter(ColorSpaces.Srgb),
            initialValue = balanceErrorColor,
            targetValue = balanceSafeColor,
            initialVelocity = balanceErrorColor
        )
    }
    val contentColorAnimatable = remember(onBalanceSafeColor, onBalanceErrorColor) {
        TargetBasedAnimation(
            animationSpec = tween(),
            typeConverter = Color.VectorConverter(ColorSpaces.Srgb),
            initialValue = onBalanceErrorColor,
            targetValue = onBalanceSafeColor,
            initialVelocity = onBalanceErrorColor
        )
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = contentColorAnimatable.getValueFromNanos(
                (contentColorAnimatable.durationNanos * balancePercentOfLimit.roundToLong())
            )
        ),
        modifier = modifier.drawBehind {
            val containerColor = containerColorAnimatable.getValueFromNanos(
                (containerColorAnimatable.durationNanos * balancePercentOfLimit.roundToLong())
            )
            drawRoundRect(
                color = containerColor.copy(alpha = ContentAlpha.PERCENT_16),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )

            drawRoundRect(
                color = containerColor,
                size = size.copy(
                    width = size.width * balancePercentOfLimit
                ),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingSmall),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Text(
                    text = stringResource(R.string.balance),
                    style = MaterialTheme.typography.titleMedium
                )

                if (showBalanceLowWarning) {
                    HorizontalSpacer(spacing = SpacingXSmall)
                    AnimatedWarning()
                }
            }
            Spacer(Modifier.height(SpacingXSmall))
            VerticalNumberSpinner(targetState = balanceAmount) {
                Text(
                    text = Formatter.currency(it),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }

    /*OverviewStat(
        title = R.string.balance,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = contentColorAnimatable.getValueFromNanos(
                (contentColorAnimatable.durationNanos * balancePercentOfLimit.roundToLong())
            )
        ),
        modifier = modifier.drawBehind {
            val containerColor = containerColorAnimatable.getValueFromNanos(
                (containerColorAnimatable.durationNanos * balancePercentOfLimit.roundToLong())
            )
            drawRoundRect(
                color = containerColor.copy(alpha = ContentAlpha.PERCENT_16),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )

            drawRoundRect(
                color = containerColor,
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
    }*/
}

@Composable
private fun AnimatedWarning(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = ContentAlpha.PERCENT_100,
        targetValue = ContentAlpha.PERCENT_32,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = WARNING_ANIM_DURATION,
                delayMillis = WARNING_ANIM_DELAY
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_warning),
        contentDescription = stringResource(R.string.content_balance_low_warning),
        tint = Color.Yellow,
        modifier = modifier
            .size(WarningSize)
            .graphicsLayer {
                this.alpha = alpha
            }
    )
}

private const val WARNING_ANIM_DURATION = 1000
private const val WARNING_ANIM_DELAY = 1000
private val WarningSize = 8.dp

@Preview(showBackground = true)
@Composable
private fun PreviewScreenContent() {
    MYMTheme {
        DashboardScreenContent(
            state = DashboardState(),
            onAddFabClick = {},
            onExpenseClick = {},
            onBottomBarActionClick = {},
            snackbarController = rememberSnackbarController(),
            navigateToSettingsWithAction = {}
        )
    }
}