package dev.ridill.mym.core.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val CornerRadiusExtraSmall = 4.dp
val CornerRadiusSmall = 8.dp
val CornerRadiusMedium = 12.dp
val CornerRadiusLarge = 16.dp
val CornerRadiusExtraLarge = 24.dp

val SpacingXSmall = 4.dp
val SpacingSmall = 8.dp
val SpacingMedium = 12.dp
val SpacingLarge = 16.dp
val SpacingLargeTop = 32.dp
val SpacingListEnd = 80.dp

val ElevationLevel0 = 0.0.dp
val ElevationLevel1 = 1.0.dp
val ElevationLevel2 = 3.0.dp
val ElevationLevel3 = 6.0.dp
val ElevationLevel4 = 8.0.dp
val ElevationLevel5 = 12.0.dp

fun defaultScreenPadding(
    top: Dp = SpacingMedium,
    start: Dp = SpacingLarge,
    end: Dp = SpacingLarge,
    bottom: Dp = SpacingMedium
): PaddingValues = PaddingValues(
    top = top,
    start = start,
    end = end,
    bottom = bottom
)

val BorderWidthDefault = 1.dp