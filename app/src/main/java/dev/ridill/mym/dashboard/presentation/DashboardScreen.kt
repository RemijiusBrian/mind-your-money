package dev.ridill.mym.dashboard.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.DashboardScreenSpec
import dev.ridill.mym.core.ui.components.HorizontalSpacer
import dev.ridill.mym.core.ui.components.MYMScaffold
import dev.ridill.mym.core.ui.components.VerticalSpacer
import dev.ridill.mym.core.ui.components.VerticalSpinner
import dev.ridill.mym.core.ui.theme.*
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.One
import kotlin.math.roundToLong

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenContent(
        isLimitSet = state.isMonthlyLimitSet,
        monthlyLimit = state.monthlyLimit,
        amountSpent = state.expenditure,
        balance = state.balanceFromLimit,
        balancePercentOfLimit = state.balancePercent
    )
}

@Composable
private fun ScreenContent(
    isLimitSet: Boolean,
    monthlyLimit: Long,
    amountSpent: Double,
    balance: Double,
    balancePercentOfLimit: Float
) {
    MYMScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(DashboardScreenSpec.label)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(mymContentPadding())
        ) {
            ExpenditureOverview(
                isLimitSet = isLimitSet,
                monthlyLimit = monthlyLimit,
                amountSpent = amountSpent,
                balance = balance,
                balancePercentOfLimit = balancePercentOfLimit
            )
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
                VerticalSpinner(targetState = monthlyLimit) {
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
                VerticalSpinner(targetState = amountSpent) {
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
        VerticalSpinner(targetState = balanceAmount) {
            Text(Formatter.currency(it))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScreenContent() {
    ExpenseTrackerTheme {
        ScreenContent(
            isLimitSet = true,
            monthlyLimit = 5_000L,
            amountSpent = 100.0,
            balance = 1_000.0,
            balancePercentOfLimit = 0.5f
        )
    }
}