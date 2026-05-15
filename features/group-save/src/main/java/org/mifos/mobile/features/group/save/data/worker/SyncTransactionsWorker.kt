package org.mifos.mobile.features.group.save.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository

@HiltWorker
class SyncTransactionsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SavingsTransactionRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncPendingTransactions()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
