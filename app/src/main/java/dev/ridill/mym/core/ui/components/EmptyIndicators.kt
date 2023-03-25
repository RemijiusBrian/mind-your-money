package dev.ridill.mym.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.SpacingXSmall

@Composable
fun ListEmptyIndicator(
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = DefaultIndicatorSize
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_expense_list_empty))
    val progress = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.4f
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress.value },
            modifier = Modifier
                .size(size)
                .then(modifier),
            contentScale = ContentScale.FillBounds
        )
        VerticalSpacer(spacing = SpacingXSmall)
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = LocalContentColor.current
                .copy(alpha = ContentAlpha.PERCENT_60)
        )
    }
}

private val DefaultIndicatorSize = 80.dp