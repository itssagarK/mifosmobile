package org.mifos.mobile.features.savings.transaction.domain.usecase

import javax.inject.Inject

data class SavingsTransactionUseCases @Inject constructor(
    val getTransactions: GetSavingsTransactionsUseCase,
    val addTransaction: AddSavingsTransactionUseCase,
    val deleteTransaction: DeleteSavingsTransactionUseCase,
    val syncPending: SyncPendingTransactionsUseCase,
    val refreshTransactions: RefreshSavingsTransactionsUseCase
)
