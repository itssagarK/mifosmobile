package org.mifos.mobile.features.savings.transaction.domain.usecase

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import org.mifos.mobile.features.savings.transaction.domain.repository.SavingsTransactionRepository
import java.util.*

class AddSavingsTransactionUseCaseTest {

    private val repository: SavingsTransactionRepository = mockk()
    private lateinit var useCase: AddSavingsTransactionUseCase

    @Before
    fun setUp() {
        useCase = AddSavingsTransactionUseCase(repository)
    }

    @Test
    fun `should call repository addTransaction`() = runTest {
        // Given
        val transaction = SavingsTransaction(1, 100.0, Date(), TransactionType.DEPOSIT, 1L, "Test")
        coEvery { repository.addTransaction(any()) } just Runs

        // When
        useCase(transaction)

        // Then
        coVerify { repository.addTransaction(transaction) }
    }
}
