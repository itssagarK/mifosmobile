package org.mifos.mobile.features.savings.transaction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.savings.transaction.data.local.entity.SavingsTransactionEntity

@Dao
interface SavingsTransactionDao {
    @Query("SELECT * FROM savings_transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getTransactions(accountId: Long): Flow<List<SavingsTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<SavingsTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SavingsTransactionEntity)

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: SavingsTransactionEntity)

    @Query("SELECT * FROM savings_transactions WHERE isPendingSync = 1")
    suspend fun getPendingTransactions(): List<SavingsTransactionEntity>

    @Query("DELETE FROM savings_transactions WHERE accountId = :accountId AND isPendingSync = 0")
    suspend fun deleteSyncedTransactions(accountId: Long)
}
