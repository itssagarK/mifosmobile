package org.mifos.mobile.features.group.save.domain.usecase

import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import androidx.paging.PagingData

class GetSavingsTransactionsUseCaseTest {

    private val repository: SavingsTransactionRepository = mockk()
    private lateinit var useCase: GetSavingsTransactionsUseCase

    @Before
    fun setUp() {
        useCase = GetSavingsTransactionsUseCase(repository)
    }

    @Test
    fun `should delegate to repository getTransactionsPaged`() = runTest {
        // Given
        every { 
            repository.getTransactionsPaged(any(), any(), any(), any(), any(), any()) 
        } returns flowOf(PagingData.empty())

        // When
        useCase(1L, "query", setOf("DEPOSIT"), SortOrder.AMOUNT_DESC, 1000L, 2000L)

        // Then
        verify { 
            repository.getTransactionsPaged(1L, "query", setOf("DEPOSIT"), SortOrder.AMOUNT_DESC, 1000L, 2000L) 
        }
    }
}
