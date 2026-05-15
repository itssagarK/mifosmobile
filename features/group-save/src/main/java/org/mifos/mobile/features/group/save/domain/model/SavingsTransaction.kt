package org.mifos.mobile.features.group.save.domain.model

import java.util.Date

data class SavingsTransaction(
    val id: Long,
    val amount: Double,
    val date: Date,
    val transactionType: TransactionType,
    val accountId: Long,
    val description: String?,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
) {
    val isPendingSync: Boolean get() = syncStatus != SyncStatus.SYNCED
}

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    INTEREST,
    FEES,
    UNKNOWN
}
