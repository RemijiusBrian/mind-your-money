package dev.ridill.mym.core.domain.model

data class MYMPreferences(
    val appFirstLaunch: Boolean,
    val theme: AppTheme,
    val materialYouTheme: Boolean,
    val monthlyLimit: Long
)