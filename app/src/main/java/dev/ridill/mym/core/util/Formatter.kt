package dev.ridill.mym.core.util

import java.text.NumberFormat
import java.util.*

object Formatter {

    fun defaultCurrencySymbol(): String = Currency.getInstance(Locale.getDefault()).symbol

    fun currency(
        value: Double,
        maxFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        groupUsed: Boolean = true
    ): String = currencyFormat().apply {
        maximumFractionDigits = maxFractionDigits
        isGroupingUsed = groupUsed
    }.format(value)

    fun currency(
        value: Long,
        maxFractionDigits: Int = DEFAULT_FRACTION_DIGITS,
        groupUsed: Boolean = true
    ): String = currencyFormat().apply {
        maximumFractionDigits = maxFractionDigits
        isGroupingUsed = groupUsed
    }.format(value)

    private fun currencyFormat(): NumberFormat =
        NumberFormat.getCurrencyInstance(Locale.getDefault())
}

private const val DEFAULT_FRACTION_DIGITS = 2