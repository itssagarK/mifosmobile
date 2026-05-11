package org.mifos.mobile.features.savings.transaction.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mifos.mobile.features.savings.transaction.data.local.dao.SavingsTransactionDao
import org.mifos.mobile.features.savings.transaction.data.mapper.toDomain
import org.mifos.mobile.features.savings.transaction.data.mapper.toDto
import org.mifos.mobile.features.savings.transaction.data.mapper.toEntity
import org.mifos.mobile.features.savings.transaction.data.remote.api.SavingsTransactionApi
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.repository.SavingsTransactionRepository
import javax.inject.Inject

class SavingsTransactionRepositoryImpl @Inject constructor(
    private val api: SavingsTransactionApi,
    private val dao: SavingsTransactionDao
) : SavingsTransactionRepository {

    override fun getTransactions(accountId: Long): Flow<List<SavingsTransaction>> {
        return dao.getTransactions(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshTransactions(accountId: Long) {
        try {
            val remoteTransactions = api.getTransactions(accountId)
            dao.deleteSyncedTransactions(accountId)
            dao.insertTransactions(remoteTransactions.map { it.toEntity() })
        } catch (e: Exception) {
            // Log error, maybe throw custom exception
            throw e
        }
    }

    override suspend fun addTransaction(transaction: SavingsTransaction) {
        val entity = transaction.copy(isPendingSync = true).toEntity()
        dao.insertTransaction(entity)
        
        try {
            syncPendingTransactions()
        } catch (e: Exception) {
            // Failed to sync, will retry later
        }
    }

    override suspend fun deleteTransaction(transaction: SavingsTransaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun syncPendingTransactions() {
        val pending = dao.getPendingTransactions()
        pending.forEach { entity ->
            try {
                val dto = entity.toDomain().toDto()
                val response = api.createTransaction(entity.accountId, dto)
                // Update local entity with remoteId and mark as synced
                dao.insertTransaction(entity.copy(
                    remoteId = response.id,
                    isPendingSync = false
                ))
            } catch (e: Exception) {
                // Ignore and continue with next
            }
        }
    }
}
