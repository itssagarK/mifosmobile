package org.mifos.mobile.features.group.save.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.usecase.SortOrder

interface SavingsTransactionRepository {
    fun getTransactions(accountId: Long): Flow<List<SavingsTransaction>>
    fun getAllTransactions(): Flow<List<SavingsTransaction>>
    
    fun getTransactionsPaged(
        accountId: Long,
        query: String = "",
        typeFilters: Set<String> = emptySet(),
        sortBy: SortOrder = SortOrder.DATE_DESC,
        startDate: Long? = null,
        endDate: Long? = null
    ): Flow<PagingData<SavingsTransaction>>

    suspend fun refreshTransactions(accountId: Long)
    suspend fun addTransaction(transaction: SavingsTransaction)
    suspend fun deleteTransaction(transaction: SavingsTransaction)
    suspend fun syncPendingTransactions()
}
