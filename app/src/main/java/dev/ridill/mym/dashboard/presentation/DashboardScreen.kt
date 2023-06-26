package dev.ridill.mym.dashboard.presentation

import androidx.annotation.FloatRange
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.ARG_QUICK_ACTION_LIMIT_UPDATE
import dev.ridill.mym.core.navigation.screenSpecs.BottomBarSpec
import dev.ridill.mym.core.navigation.screenSpecs.DashboardScreenSpec
import dev.ridill.mym.core.ui.components.ExpenseCard
import dev.ridill.mym.core.ui.components.FadeVisibility
import dev.ridill.mym.core.ui.components.HorizontalSpacer
import dev.ridill.mym.core.ui.components.LabelText
import dev.ridill.mym.core.ui.components.MYMScaffold
import dev.ridill.mym.core.ui.components.OnLifecycleStartEffect
import dev.ridill.mym.core.ui.components.SnackbarController
import dev.ridill.mym.core.ui.components.VerticalNumberSpinner
import dev.ridill.mym.core.ui.components.VerticalSpacer
import dev.ridill.mym.core.ui.components.VerticalTitleAndValue
import dev.ridill.mym.core.ui.components.rememberSnackbarController
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.ElevationLevel1
import dev.ridill.mym.core.ui.theme.MYMTheme
import dev.ridill.mym.core.ui.theme.SpacingLarge
import dev.ridill.mym.core.ui.theme.SpacingListEnd
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.ui.theme.SpacingXSmall
import dev.ridill.mym.core.ui.theme.defaultScreenPadding
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.core.util.logD
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
                                imageVector = ImageVector.vectorResource(spec.iconRes),
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
                item(key = "Greeting") {
                    Greeting(
                        modifier = Modifier
                            .animateItemPlacement()
                    )
                }
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

            FadeVisibility(
                visible = showScrollUpButton,
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
private fun Greeting(
    modifier: Modifier = Modifier
) {
    var timeOfDayLabelRes by remember { mutableIntStateOf(-1) }

    OnLifecycleStartEffect {
        timeOfDayLabelRes = DateUtil.getPartOfDay().labelRes
        logD { timeOfDayLabelRes }
    }

    Column(
        modifier = modifier
    ) {
        if (timeOfDayLabelRes != -1) {
            Text(
                text = stringResource(
                    R.string.greeting_part_of_day,
                    stringResource(timeOfDayLabelRes)
                ),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun ExpenditureOverview(
    monthlyLimit: Long,
    amountSpent: Double,
    balance: Double,
    @FloatRange(from = 0.0, to = 1.0) balancePercentOfLimit: Float,
    showBalanceLowWarning: Boolean,
    onLimitCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val balanceSafeColor = MaterialTheme.colorScheme.secondary
    val balanceErrorColor = MaterialTheme.colorScheme.error
    val balanceColorAnimation = remember(balanceSafeColor, balanceErrorColor) {
        TargetBasedAnimation(
            animationSpec = tween(),
            typeConverter = Color.VectorConverter(ColorSpaces.Srgb),
            initialValue = balanceErrorColor,
            targetValue = balanceSafeColor,
            initialVelocity = balanceErrorColor
        )
    }
    val balanceColor by remember(balancePercentOfLimit) {
        derivedStateOf {
            balanceColorAnimation.getValueFromNanos(
                (balanceColorAnimation.durationNanos * balancePercentOfLimit).roundToLong()
            )
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        VerticalTitleAndValue(
            title = stringResource(R.string.you_have_spent),
        ) {
            VerticalNumberSpinner(targetState = amountSpent) {
                Text(
                    text = Formatter.currency(it),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        VerticalSpacer(spacing = SpacingMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(Float.One)
            ) {
                Row {
                    if (showBalanceLowWarning) {
                        AnimatedWarning()
                        HorizontalSpacer(spacing = SpacingXSmall)
                    }
                    VerticalTitleAndValue(
                        title = stringResource(R.string.you_have_balance),
                        titleStyle = MaterialTheme.typography.titleSmall,
                        valueStyle = MaterialTheme.typography.titleSmall
                    ) {
                        VerticalNumberSpinner(targetState = balance) {
                            Text(
                                text = Formatter.currency(it),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = balanceColor
                            )
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = balancePercentOfLimit,
                    color = balanceColor,
                    strokeCap = StrokeCap.Round
                )
            }
            HorizontalSpacer(spacing = SpacingMedium)
            Surface(
                modifier = Modifier
                    .weight(Float.One),
                onClick = onLimitCardClick,
                shape = MaterialTheme.shapes.medium
            ) {
                VerticalTitleAndValue(
                    title = stringResource(R.string.left_from),
                    titleStyle = MaterialTheme.typography.titleSmall,
                    valueStyle = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(SpacingSmall)
                ) {
                    VerticalNumberSpinner(targetState = monthlyLimit) {
                        Text(
                            text = Formatter.currency(it),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/*@Composable
private fun BalanceCard(
    balanceAmount: Double,
    @FloatRange(0.0, 1.0) balancePercentOfLimit: Float,
    showBalanceLowWarning: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadiusMedium
) {
    val balanceSafeColor = MaterialTheme.colorScheme.primary
    val onBalanceSafeColor = contentColorFor(balanceSafeColor)
    val balanceErrorColor = MaterialTheme.colorScheme.error
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
    val containerColor by remember {
        derivedStateOf {
            containerColorAnimatable.getValueFromNanos(
                (containerColorAnimatable.durationNanos * balancePercentOfLimit).roundToLong()
            )
        }
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
                (contentColorAnimatable.durationNanos * balancePercentOfLimit).roundToLong()
            )
        ),
        modifier = modifier
            .drawBehind {
                drawRect(
                    color = containerColor
                        .copy(alpha = ContentAlpha.PERCENT_16)
                )
                drawRoundRect(
                    color = containerColor,
                    size = size.copy(
                        width = size.width * (balancePercentOfLimit.takeIf { it > Float.Zero }
                            ?: Float.One)
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
                if (showBalanceLowWarning) {
                    AnimatedWarning()
                    HorizontalSpacer(spacing = SpacingXSmall)
                }
                Text(
                    text = stringResource(R.string.you_have_balance),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            VerticalSpacer(spacing = SpacingSmall)
            VerticalNumberSpinner(targetState = balanceAmount) {
                Text(
                    text = Formatter.currency(it),
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}*/

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
            state = DashboardState.INITIAL,
            onAddFabClick = {},
            onExpenseClick = {},
            onBottomBarActionClick = {},
            snackbarController = rememberSnackbarController(),
            navigateToSettingsWithAction = {}
        )
    }
}