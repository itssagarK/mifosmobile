package org.mifos.mobile.features.group.save.domain.usecase

import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import javax.inject.Inject

class AddSavingsTransactionUseCase @Inject constructor(
    private val repository: SavingsTransactionRepository
) {
    suspend operator fun invoke(transaction: SavingsTransaction) {
        repository.addTransaction(transaction)
    }
}
