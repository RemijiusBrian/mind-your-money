package dev.ridill.mym.expenses.domain.sms

import dev.ridill.mym.core.util.logD

class PaymentSmsService {

    fun isMerchantSms(address: String?): Boolean =
        address?.matches(MERCHANT_SENDER_PATTERN.toRegex()) ?: false

    fun isSmsForMonetaryDebit(content: String): Boolean =
        content.contains(DEBIT_PATTERN.toRegex())

    private fun extractAmount(body: String): String? =
        AMOUNT_PATTERN.toRegex().find(body)?.groups?.get(1)?.value

    private fun extractMerchantName(content: String): String? =
        MERCHANT_PATTERN.toRegex().find(content)?.groups?.get(1)?.value

    @Throws(AmountExtractionThrowable::class)
    fun extractPaymentDetails(content: String): PaymentDetails {
        val amount = extractAmount(content) ?: throw AmountExtractionThrowable()
        logD { "Amount extracted from sms - $amount" }
        val merchant = extractMerchantName(content) ?: "at Merchant"
        logD { "Merchant extracted from sms - $merchant" }

        return PaymentDetails(
            amount = amount,
            merchant = merchant
        )
    }
}

data class PaymentDetails(
    val amount: String,
    val merchant: String
)

private const val MERCHANT_SENDER_PATTERN = "[a-zA-Z0-9]{2}-[a-zA-Z0-9]{6}"
private const val DEBIT_PATTERN = "(?i)spent|debited"
private const val AMOUNT_PATTERN =
    "(?i)(?:RS|INR|MRP)\\.?\\s?(\\d+(:?,\\d+)?(,\\d+)?(\\.\\d{1,2})?)"
private const val MERCHANT_PATTERN =
    "(?i)(?:\\sat\\s|in\\*)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)"

class AmountExtractionThrowable : Throwable("Amount Extraction Failed")