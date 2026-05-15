package org.mifos.mobile.features.group.save.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import javax.inject.Inject

class GetSavingsTransactionsUseCase @Inject constructor(
    private val repository: SavingsTransactionRepository
) {
    operator fun invoke(
        accountId: Long, 
        query: String = "",
        typeFilters: Set<String> = emptySet(),
        sortBy: SortOrder = SortOrder.DATE_DESC,
        startDate: Long? = null,
        endDate: Long? = null
    ): Flow<PagingData<SavingsTransaction>> {
        return repository.getTransactionsPaged(
            accountId = accountId,
            query = query,
            typeFilters = typeFilters,
            sortBy = sortBy,
            startDate = startDate,
            endDate = endDate
        )
    }
}

enum class SortOrder {
    DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
}
