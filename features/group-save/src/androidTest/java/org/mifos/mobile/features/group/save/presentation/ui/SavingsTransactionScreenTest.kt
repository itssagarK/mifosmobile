package org.mifos.mobile.features.group.save.presentation.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.group.save.presentation.viewmodel.SavingsTransactionViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import java.util.*

class SavingsTransactionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: SavingsTransactionViewModel = mockk(relaxed = true)

    @Test
    fun shouldDisplayTitleAndAddButton() {
        // Given
        every { viewModel.summaryState } returns MutableStateFlow(UiState.Loading)
        every { viewModel.transactionsPaged } returns flowOf(PagingData.empty())
        every { viewModel.searchQuery } returns MutableStateFlow("")
        every { viewModel.selectedTypes } returns MutableStateFlow(emptySet())
        every { viewModel.lastUpdated } returns MutableStateFlow(null)
        every { viewModel.savingsGoal } returns MutableStateFlow(2000.0)
        every { viewModel.dateRange } returns MutableStateFlow(null to null)

        // When
        composeTestRule.setContent {
            SavingsTransactionScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Savings Transactions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Transaction").assertIsDisplayed()
    }

    @Test
    fun shouldShowAddDialogWhenAddButtonClicked() {
        // Given
        every { viewModel.summaryState } returns MutableStateFlow(UiState.Loading)
        every { viewModel.transactionsPaged } returns flowOf(PagingData.empty())
        every { viewModel.searchQuery } returns MutableStateFlow("")
        every { viewModel.selectedTypes } returns MutableStateFlow(emptySet())
        every { viewModel.lastUpdated } returns MutableStateFlow(null)
        every { viewModel.savingsGoal } returns MutableStateFlow(2000.0)
        every { viewModel.dateRange } returns MutableStateFlow(null to null)

        // When
        composeTestRule.setContent {
            SavingsTransactionScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText("Add Transaction").performClick()

        // Then
        composeTestRule.onNodeWithText("New Transaction").assertIsDisplayed()
    }
}
