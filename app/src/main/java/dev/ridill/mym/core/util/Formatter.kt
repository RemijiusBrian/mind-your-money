package dev.ridill.mym.core.util

import java.text.NumberFormat
import java.util.*

object Formatter {

    fun defaultCurrencySymbol(): String = Currency.getInstance(Locale.getDefault()).symbol

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