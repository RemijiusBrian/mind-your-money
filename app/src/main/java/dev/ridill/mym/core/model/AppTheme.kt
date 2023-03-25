package dev.ridill.mym.core.model

import androidx.annotation.StringRes
import dev.ridill.mym.R

enum class AppTheme(
    @StringRes val label: Int
) {
    DYNAMIC(R.string.theme_dynamic),
    SYSTEM_DEFAULT(R.string.theme_system_default),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark)
}