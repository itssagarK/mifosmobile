package org.mifos.mobile.features.savings.transaction.domain.model

import java.util.Date

data class SavingsTransaction(
    val id: Long,
    val amount: Double,
    val date: Date,
    val transactionType: TransactionType,
    val accountId: Long,
    val description: String?,
    val isPendingSync: Boolean = false
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    INTEREST,
    FEES,
    UNKNOWN
}
