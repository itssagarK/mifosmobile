package org.mifos.mobile.features.group.save.domain.usecase

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository

class SyncPendingTransactionsUseCaseTest {

    private val repository: SavingsTransactionRepository = mockk()
    private lateinit var useCase: SyncPendingTransactionsUseCase

    @Before
    fun setUp() {
        useCase = SyncPendingTransactionsUseCase(repository)
    }

    @Test
    fun `should call repository syncPendingTransactions`() = runTest {
        // Given
        coEvery { repository.syncPendingTransactions() } just Runs

        // When
        useCase()

        // Then
        coVerify { repository.syncPendingTransactions() }
    }
}
