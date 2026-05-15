package org.mifos.mobile.features.group.save.domain.usecase

import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import javax.inject.Inject

class SyncPendingTransactionsUseCase @Inject constructor(
    private val repository: SavingsTransactionRepository
) {
    suspend operator fun invoke() {
        repository.syncPendingTransactions()
    }
}
