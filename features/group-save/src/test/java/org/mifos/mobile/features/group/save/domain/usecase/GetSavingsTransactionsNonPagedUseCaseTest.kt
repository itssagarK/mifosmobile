package org.mifos.mobile.features.group.save.domain.usecase

import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import java.util.*
import org.junit.Assert.assertEquals

class GetSavingsTransactionsNonPagedUseCaseTest {

    private val repository: SavingsTransactionRepository = mockk()
    private lateinit var useCase: GetSavingsTransactionsNonPagedUseCase

    @Before
    fun setUp() {
        useCase = GetSavingsTransactionsNonPagedUseCase(repository)
    }

    @Test
    fun `should filter transactions by type and query`() = runTest {
        // Given
        val transactions = listOf(
            SavingsTransaction(1, 100.0, Date(), TransactionType.DEPOSIT, 1L, "Apple"),
            SavingsTransaction(2, 50.0, Date(), TransactionType.WITHDRAWAL, 1L, "Banana"),
            SavingsTransaction(3, 10.0, Date(), TransactionType.FEES, 1L, "Service Fee")
        )
        every { repository.getTransactions(any()) } returns flowOf(transactions)

        // When filtering by type WITHDRAWAL
        val typeResult = useCase(1L, "", setOf("WITHDRAWAL")).first()
        assertEquals(1, typeResult.size)
        assertEquals(2L, typeResult[0].id)

        // When filtering by query "Apple"
        val queryResult = useCase(1L, "Apple", emptySet()).first()
        assertEquals(1, queryResult.size)
        assertEquals(1L, queryResult[0].id)

        // When filtering by type DEPOSIT and query "Apple"
        val bothResult = useCase(1L, "Apple", setOf("DEPOSIT")).first()
        assertEquals(1, bothResult.size)
        
        // When filtering by type FEES and query "Apple" (should be empty)
        val emptyResult = useCase(1L, "Apple", setOf("FEES")).first()
        assertEquals(0, emptyResult.size)
    }
}
