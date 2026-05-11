package org.mifos.mobile.features.savings.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import org.mifos.mobile.features.savings.transaction.domain.usecase.SavingsTransactionUseCases
import org.mifos.mobile.features.savings.transaction.domain.usecase.SortOrder
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SavingsTransactionViewModel @Inject constructor(
    private val useCases: SavingsTransactionUseCases
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow("ALL")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _dateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null)
    val dateRange: StateFlow<Pair<Long?, Long?>> = _dateRange.asStateFlow()

    private val _savingsGoal = MutableStateFlow(2000.0)
    val savingsGoal: StateFlow<Double> = _savingsGoal.asStateFlow()

    private val _refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Idle)

    private val _lastUpdated = MutableStateFlow<Long?>(null)
    val lastUpdated: StateFlow<Long?> = _lastUpdated.asStateFlow()

    val uiState: StateFlow<UiState<List<SavingsTransaction>>> = combine(
        _searchQuery,
        _selectedType,
        _sortOrder,
        _dateRange
    ) { query, type, sort, range ->
        Quartet(query, type, sort, range)
    }.flatMapLatest { (query, type, sort, range) ->
        useCases.getTransactions(1L, query, type, sort, range.first, range.second)
    }.combine(_refreshStatus) { transactions, status ->
            when {
                status is RefreshStatus.Loading && transactions.isEmpty() -> UiState.Loading
                status is RefreshStatus.Error && transactions.isEmpty() -> UiState.Error(status.message)
                else -> {
                    if (transactions.isEmpty() && status !is RefreshStatus.Loading) {
                        UiState.Empty
                    } else {
                        UiState.Success(transactions)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    init {
        refreshTransactions()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTypeFilterChanged(type: String) {
        _selectedType.value = type
    }

    fun onSortOrderChanged(order: SortOrder) {
        _sortOrder.value = order
    }

    fun refreshTransactions() {
        viewModelScope.launch {
            _refreshStatus.value = RefreshStatus.Loading
            try {
                useCases.refreshTransactions(1L)
                _refreshStatus.value = RefreshStatus.Idle
                _lastUpdated.value = System.currentTimeMillis()
            } catch (e: Exception) {
                _refreshStatus.value = RefreshStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            useCases.syncPending()
        }
    }

    fun retry() {
        refreshTransactions()
    }

    fun addTransaction(amount: Double, type: TransactionType, description: String) {
        viewModelScope.launch {
            val transaction = SavingsTransaction(
                id = 0,
                amount = amount,
                date = Date(),
                transactionType = type,
                accountId = 1L,
                description = description,
                isPendingSync = true
            )
            useCases.addTransaction(transaction)
        }
    }

    private var lastDeletedTransaction: SavingsTransaction? = null

    fun deleteTransaction(transaction: SavingsTransaction) {
        viewModelScope.launch {
            lastDeletedTransaction = transaction
            useCases.deleteTransaction(transaction)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            lastDeletedTransaction?.let {
                useCases.addTransaction(it)
                lastDeletedTransaction = null
            }
        }
    }

    fun updateSavingsGoal(goal: Double) {
        _savingsGoal.value = goal
    }

    fun updateDateRange(start: Long?, end: Long?) {
        _dateRange.value = start to end
    }

    private data class Quartet<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    private sealed interface RefreshStatus {
        data object Idle : RefreshStatus
        data object Loading : RefreshStatus
        data class Error(val message: String) : RefreshStatus
    }
}
