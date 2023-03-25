package dev.ridill.mym.core.util

import java.text.NumberFormat

object Formatter {
    fun currency(
        value: Double,
        maxFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        groupUsed: Boolean = true
    ): String = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = maxFractionDigits
        isGroupingUsed = groupUsed
    }.format(value)

    fun currency(
        value: Long,
        maxFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        groupUsed: Boolean = true
    ): String = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = maxFractionDigits
        isGroupingUsed = groupUsed
    }.format(value)
}

private const val DEFAULT_FRACTION_DIGITS = 2