package org.mifos.mobile.features.savings.transaction.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import org.mifos.mobile.features.savings.transaction.domain.usecase.*
import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SavingsTransactionViewModelTest {

    private val useCases: SavingsTransactionUseCases = mockk()
    private val getTransactions: GetSavingsTransactionsUseCase = mockk()
    private val addTransaction: AddSavingsTransactionUseCase = mockk()
    private val deleteTransaction: DeleteSavingsTransactionUseCase = mockk()
    private val syncPending: SyncPendingTransactionsUseCase = mockk()
    private val refreshTransactions: RefreshSavingsTransactionsUseCase = mockk()
    
    private lateinit var viewModel: SavingsTransactionViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        every { useCases.getTransactions } returns getTransactions
        every { useCases.addTransaction } returns addTransaction
        every { useCases.deleteTransaction } returns deleteTransaction
        every { useCases.syncPending } returns syncPending
        every { useCases.refreshTransactions } returns refreshTransactions

        every { getTransactions(any(), any(), any(), any(), any(), any()) } returns flowOf(emptyList())
        coEvery { refreshTransactions(any()) } just Runs
        
        viewModel = SavingsTransactionViewModel(useCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Success with transactions when usecase has data`() = runTest {
        // Given
        val transactions = listOf(
            SavingsTransaction(1, 100.0, Date(), TransactionType.DEPOSIT, 1L, "Test")
        )
        every { getTransactions(any(), any(), any(), any(), any(), any()) } returns flowOf(transactions)
        
        // When
        viewModel = SavingsTransactionViewModel(useCases)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(transactions, (state as UiState.Success).data)
        }
    }

    @Test
    fun `syncAll should trigger usecase sync`() = runTest {
        // Given
        coEvery { syncPending() } just Runs

        // When
        viewModel.syncAll()

        // Then
        coVerify { syncPending() }
    }

    @Test
    fun `addTransaction should trigger usecase add`() = runTest {
        // Given
        coEvery { addTransaction(any()) } just Runs

        // When
        viewModel.addTransaction(100.0, TransactionType.DEPOSIT, "Salary")

        // Then
        coVerify { addTransaction(match { 
            it.amount == 100.0 && it.transactionType == TransactionType.DEPOSIT 
        }) }
    }

    @Test
    fun `refresh failure should show error state when transactions are empty`() = runTest {
        // Given
        every { getTransactions(any(), any(), any(), any(), any(), any()) } returns flowOf(emptyList())
        coEvery { refreshTransactions(any()) } throws Exception("Network Error")
        
        // When
        viewModel = SavingsTransactionViewModel(useCases)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            assertEquals("Network Error", (state as UiState.Error).message)
        }
    }
}
