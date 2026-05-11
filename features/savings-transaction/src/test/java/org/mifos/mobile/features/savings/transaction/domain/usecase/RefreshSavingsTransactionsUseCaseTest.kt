package org.mifos.mobile.features.savings.transaction.domain.usecase

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.savings.transaction.domain.repository.SavingsTransactionRepository

class RefreshSavingsTransactionsUseCaseTest {

    private val repository: SavingsTransactionRepository = mockk()
    private lateinit var useCase: RefreshSavingsTransactionsUseCase

    @Before
    fun setUp() {
        useCase = RefreshSavingsTransactionsUseCase(repository)
    }

    @Test
    fun `should call repository refreshTransactions`() = runTest {
        // Given
        val accountId = 1L
        coEvery { repository.refreshTransactions(accountId) } just Runs

        // When
        useCase(accountId)

        // Then
        coVerify { repository.refreshTransactions(accountId) }
    }
}
