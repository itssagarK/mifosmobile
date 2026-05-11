package org.mifos.mobile.features.savings.transaction.data.repository

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mifos.mobile.features.savings.transaction.data.local.dao.SavingsTransactionDao
import org.mifos.mobile.features.savings.transaction.data.remote.api.SavingsTransactionApi
import org.mifos.mobile.features.savings.transaction.data.remote.model.SavingsTransactionDto

@OptIn(ExperimentalCoroutinesApi::class)
class SavingsTransactionRepositoryImplTest {

    private lateinit var repository: SavingsTransactionRepositoryImpl
    private val api: SavingsTransactionApi = mockk()
    private val dao: SavingsTransactionDao = mockk()

    @Before
    fun setUp() {
        repository = SavingsTransactionRepositoryImpl(api, dao)
    }

    @Test
    fun `refreshTransactions should fetch from api and insert into dao`() = runTest {
        // Given
        val accountId = 1L
        val remoteTransactions = listOf(
            SavingsTransactionDto(1, 100.0, 123456789, "DEPOSIT", accountId, "Test")
        )
        coEvery { api.getTransactions(accountId) } returns remoteTransactions
        coEvery { dao.deleteSyncedTransactions(accountId) } just Runs
        coEvery { dao.insertTransactions(any()) } just Runs

        // When
        repository.refreshTransactions(accountId)

        // Then
        coVerify { api.getTransactions(accountId) }
        coVerify { dao.deleteSyncedTransactions(accountId) }
        coVerify { dao.insertTransactions(any()) }
    }
}
