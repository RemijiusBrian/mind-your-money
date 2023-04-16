package dev.ridill.mym.core.util

import android.icu.text.CompactDecimalFormat
import android.icu.text.CompactDecimalFormat.CompactStyle
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

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

    fun percentage(value: Float): String = NumberFormat.getPercentInstance(Locale.getDefault())
        .format(value)

    fun compactCurrency(
        number: Double,
        style: CompactStyle = CompactStyle.SHORT,
        maxFractionDigits: Int = DEFAULT_FRACTION_DIGITS
    ): String = CompactDecimalFormat.getInstance(
        Locale.getDefault(),
        style
    ).apply {
        maximumFractionDigits = maxFractionDigits
        this.currency = android.icu.util.Currency.getInstance(Locale.getDefault())
    }.format(number)
}

private const val DEFAULT_FRACTION_DIGITS = 2