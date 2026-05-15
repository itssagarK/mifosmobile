package org.mifos.mobile.features.group.save.domain.usecase

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import java.util.*

class DeleteSavingsTransactionUseCaseTest {

    private val repository: SavingsTransactionRepository = mockk()
    private lateinit var useCase: DeleteSavingsTransactionUseCase

    @Before
    fun setUp() {
        useCase = DeleteSavingsTransactionUseCase(repository)
    }

    @Test
    fun `should call repository deleteTransaction`() = runTest {
        // Given
        val transaction = SavingsTransaction(1, 100.0, Date(), TransactionType.DEPOSIT, 1L, "Test")
        coEvery { repository.deleteTransaction(any()) } just Runs

        // When
        useCase(transaction)

        // Then
        coVerify { repository.deleteTransaction(transaction) }
    }
}
