package dev.ridill.mym.core.util

import android.icu.text.CompactDecimalFormat
import android.icu.text.NumberFormat
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
        CompactDecimalFormat.getCurrencyInstance(Locale.getDefault())
}

private const val DEFAULT_FRACTION_DIGITS = 2