package org.mifos.mobile.features.group.save.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import javax.inject.Inject

class GetSavingsTransactionsNonPagedUseCase @Inject constructor(
    private val repository: SavingsTransactionRepository
) {
    operator fun invoke(
        accountId: Long, 
        query: String = "",
        typeFilters: Set<String> = emptySet(),
        sortBy: SortOrder = SortOrder.DATE_DESC,
        startDate: Long? = null,
        endDate: Long? = null
    ): Flow<List<SavingsTransaction>> {
        return repository.getTransactions(accountId).map { transactions ->
            val filtered = transactions.filter { transaction ->
                val matchesType = typeFilters.isEmpty() || typeFilters.contains(transaction.transactionType.name)
                val matchesQuery = query.isEmpty() || (
                    transaction.description?.contains(query, ignoreCase = true) == true ||
                    transaction.transactionType.name.contains(query, ignoreCase = true) ||
                    transaction.amount.toString().contains(query)
                )
                val matchesDate = (startDate == null || transaction.date.time >= startDate) &&
                                 (endDate == null || transaction.date.time <= endDate)
                
                matchesType && matchesQuery && matchesDate
            }

            when (sortBy) {
                SortOrder.DATE_DESC -> filtered.sortedByDescending { it.date }
                SortOrder.DATE_ASC -> filtered.sortedBy { it.date }
                SortOrder.AMOUNT_DESC -> filtered.sortedByDescending { it.amount }
                SortOrder.AMOUNT_ASC -> filtered.sortedBy { it.amount }
            }
        }
    }
}
