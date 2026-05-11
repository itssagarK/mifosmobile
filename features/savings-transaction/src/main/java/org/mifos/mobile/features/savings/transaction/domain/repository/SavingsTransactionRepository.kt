package org.mifos.mobile.features.savings.transaction.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction

interface SavingsTransactionRepository {
    fun getTransactions(accountId: Long): Flow<List<SavingsTransaction>>
    suspend fun refreshTransactions(accountId: Long)
    suspend fun addTransaction(transaction: SavingsTransaction)
    suspend fun deleteTransaction(transaction: SavingsTransaction)
    suspend fun syncPendingTransactions()
}
