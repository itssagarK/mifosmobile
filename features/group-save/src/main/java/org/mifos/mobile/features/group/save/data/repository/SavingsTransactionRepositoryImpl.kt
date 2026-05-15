package org.mifos.mobile.features.group.save.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mifos.mobile.features.group.save.data.local.dao.SavingsTransactionDao
import org.mifos.mobile.features.group.save.data.mapper.toDomain
import org.mifos.mobile.features.group.save.data.mapper.toDto
import org.mifos.mobile.features.group.save.data.mapper.toEntity
import org.mifos.mobile.features.group.save.data.remote.api.SavingsTransactionApi
import org.mifos.mobile.features.group.save.data.worker.SyncTransactionsWorker
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import org.mifos.mobile.features.group.save.domain.usecase.SortOrder
import javax.inject.Inject

class SavingsTransactionRepositoryImpl @Inject constructor(
    private val api: SavingsTransactionApi,
    private val dao: SavingsTransactionDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) : SavingsTransactionRepository {

    override fun getTransactions(accountId: Long): Flow<List<SavingsTransaction>> {
        return dao.getTransactions(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllTransactions(): Flow<List<SavingsTransaction>> {
        return dao.getAllTransactionsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsPaged(
        accountId: Long,
        query: String,
        typeFilters: Set<String>,
        sortBy: SortOrder,
        startDate: Long?,
        endDate: Long?
    ): Flow<PagingData<SavingsTransaction>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dao.getTransactionsPaged(
                    accountId = accountId,
                    query = query,
                    useTypeFilter = typeFilters.isNotEmpty(),
                    typeFilters = typeFilters.toList(),
                    sortBy = sortBy.name,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
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
        val entity = transaction.copy(syncStatus = SyncStatus.PENDING_SYNC).toEntity()
        dao.insertTransaction(entity)
        
        try {
            syncPendingTransactions()
        } catch (e: Exception) {
            scheduleSyncWork()
        }
    }

    override suspend fun deleteTransaction(transaction: SavingsTransaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun syncPendingTransactions() {
        val pending = dao.getPendingTransactions()
        pending.forEach { entity ->
            try {
                // Mark as SYNCING
                dao.insertTransaction(entity.copy(syncStatus = SyncStatus.SYNCING.name))
                
                val dto = entity.toDomain().toDto()
                val response = api.createTransaction(entity.accountId, dto)
                
                // Mark as SYNCED
                dao.insertTransaction(entity.copy(
                    remoteId = response.id,
                    syncStatus = SyncStatus.SYNCED.name
                ))
            } catch (e: Exception) {
                // Mark as FAILED
                dao.insertTransaction(entity.copy(syncStatus = SyncStatus.FAILED.name))
                scheduleSyncWork()
            }
        }
        userPreferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
    }
    
    private fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncTransactionsWorker>()
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
}
