package org.mifos.mobile.features.group.save.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.group.save.data.local.entity.SavingsTransactionEntity

@Dao
interface SavingsTransactionDao {
    @Query("SELECT * FROM savings_transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getTransactions(accountId: Long): Flow<List<SavingsTransactionEntity>>

    @Query("""
        SELECT * FROM savings_transactions 
        WHERE accountId = :accountId 
        AND (:useTypeFilter = 0 OR type IN (:typeFilters))
        AND (:query = '' OR (description IS NOT NULL AND description LIKE '%' || :query || '%') OR type LIKE '%' || :query || '%' OR CAST(amount AS TEXT) LIKE '%' || :query || '%')
        AND (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        ORDER BY 
        CASE WHEN :sortBy = 'DATE_DESC' THEN date END DESC,
        CASE WHEN :sortBy = 'DATE_ASC' THEN date END ASC,
        CASE WHEN :sortBy = 'AMOUNT_DESC' THEN amount END DESC,
        CASE WHEN :sortBy = 'AMOUNT_ASC' THEN amount END ASC,
        date DESC -- secondary sort to ensure deterministic order
    """)
    fun getTransactionsPaged(
        accountId: Long,
        query: String,
        useTypeFilter: Boolean,
        typeFilters: List<String>,
        sortBy: String,
        startDate: Long?,
        endDate: Long?
    ): PagingSource<Int, SavingsTransactionEntity>

    @Query("SELECT * FROM savings_transactions ORDER BY date DESC")
    fun getAllTransactionsFlow(): Flow<List<SavingsTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<SavingsTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SavingsTransactionEntity)

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: SavingsTransactionEntity)

    @Query("SELECT * FROM savings_transactions WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingTransactions(): List<SavingsTransactionEntity>

    @Query("DELETE FROM savings_transactions WHERE accountId = :accountId AND syncStatus = 'SYNCED'")
    suspend fun deleteSyncedTransactions(accountId: Long)
}
