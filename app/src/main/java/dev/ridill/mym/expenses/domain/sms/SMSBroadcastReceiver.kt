package dev.ridill.mym.expenses.domain.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import dagger.hilt.android.AndroidEntryPoint
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.core.util.logI
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.di.ApplicationScope
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SMSBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    @ApplicationScope
    @Inject
    lateinit var applicationScope: CoroutineScope

//    @Inject
//    lateinit var notificationHelper: NotificationHelper<Expense>

    @Inject
    lateinit var smsService: PaymentSmsService

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || intent.action != "android.provider.Telephony.SMS_RECEIVED") return
        logI { "SMS Received" }
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            .ifEmpty { return }

        applicationScope.launch {
            for (message in messages) {
                if (!smsService.isMerchantSms(message.originatingAddress)) continue

                val body = message.messageBody
                if (!smsService.isSmsForMonetaryDebit(body)) continue
                val paymentDetails = tryOrNull { smsService.extractPaymentDetails(body) }
                    ?: continue
                savePaymentDetails(paymentDetails)
            }
        }
    }

    private suspend fun savePaymentDetails(paymentDetails: PaymentDetails) {
        val expense = Expense(
            id = Long.Zero,
            note = paymentDetails.merchant,
            amount = paymentDetails.amount,
            dateTime = DateUtil.currentDateTime(),
            tagName = null
        )
        val insertedId = expenseRepository.insert(expense)
//        notificationHelper.showNotification(expense.copy(id = insertedId))
    }
}