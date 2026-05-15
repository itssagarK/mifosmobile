package org.mifos.mobile.features.group.save.domain.usecase

import javax.inject.Inject

data class SavingsTransactionUseCases @Inject constructor(
    val getTransactions: GetSavingsTransactionsUseCase,
    val getTransactionsNonPaged: GetSavingsTransactionsNonPagedUseCase,
    val addTransaction: AddSavingsTransactionUseCase,
    val deleteTransaction: DeleteSavingsTransactionUseCase,
    val syncPending: SyncPendingTransactionsUseCase,
    val refreshTransactions: RefreshSavingsTransactionsUseCase
)
