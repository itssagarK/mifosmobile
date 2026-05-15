package org.mifos.mobile.features.group.save.presentation.viewmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.core.common.NetworkMonitor
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import org.mifos.mobile.features.group.save.domain.usecase.SavingsTransactionUseCases
import org.mifos.mobile.features.group.save.domain.usecase.SortOrder
import org.mifos.mobile.features.group.save.data.repository.UserPreferencesRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SavingsTransactionViewModel @Inject constructor(
    private val useCases: SavingsTransactionUseCases,
    private val preferencesRepository: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedTypes: StateFlow<Set<String>> = _selectedTypes.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { preferences ->
                _sortOrder.value = preferences.sortOrder
                _selectedTypes.value = preferences.selectedTypes
            }
        }
    }

    private val _dateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null)
    val dateRange: StateFlow<Pair<Long?, Long?>> = _dateRange.asStateFlow()

    private val _savingsGoal = MutableStateFlow(2000.0)
    val savingsGoal: StateFlow<Double> = _savingsGoal.asStateFlow()

    private val _offlineStatus = MutableStateFlow<OfflineStatus>(OfflineStatus.Online)
    val offlineStatus: StateFlow<OfflineStatus> = _offlineStatus.asStateFlow()

    private val _refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Idle)

    val lastUpdated: StateFlow<Long?> = preferencesRepository.userPreferencesFlow
        .map { it.lastSyncTimestamp.takeIf { t -> t > 0 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Summary state for components that need the full (filtered) list
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val summaryState: StateFlow<UiState<List<SavingsTransaction>>> = combine(
        _searchQuery,
        _selectedTypes,
        _sortOrder,
        _dateRange
    ) { query, types, sort, range ->
        Quartet(query, types, sort, range)
    }.flatMapLatest { (query, types, sort, range) ->
        useCases.getTransactionsNonPaged(1L, query, types, sort, range.first, range.second)
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

    val transactions: Flow<List<SavingsTransaction>> = summaryState.map { state ->
        if (state is UiState.Success) state.data else emptyList()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val operationalMetrics: StateFlow<OperationalMetrics> = combine(summaryState, lastUpdated) { state, lastSync ->
        when (state) {
            is UiState.Success -> {
                val transactions = state.data
                OperationalMetrics(
                    activeGroups = 18,
                    todayMeetings = 4,
                    pendingCollections = transactions.count { it.syncStatus != SyncStatus.SYNCED },
                    offlineQueueCount = transactions.count { it.syncStatus != SyncStatus.SYNCED } + 2,
                    syncingCount = transactions.count { it.syncStatus == SyncStatus.SYNCING },
                    failedCount = transactions.count { it.syncStatus == SyncStatus.FAILED },
                    lastSyncTime = lastSync
                )
            }
            else -> OperationalMetrics(
                activeGroups = 18,
                todayMeetings = 4,
                offlineQueueCount = 2,
                lastSyncTime = lastSync
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OperationalMetrics(
            activeGroups = 18,
            todayMeetings = 4,
            offlineQueueCount = 2
        )
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val syncState: StateFlow<SyncState> = operationalMetrics.mapLatest { metrics ->
        when {
            metrics.syncingCount > 0 -> SyncState.Syncing(metrics.syncingCount)
            metrics.failedCount > 0 -> SyncState.RetryFailed(metrics.failedCount)
            metrics.pendingCollections > 0 -> SyncState.PendingSync(metrics.pendingCollections)
            else -> SyncState.SyncedSuccessfully(metrics.lastSyncTime)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SyncState.SyncedSuccessfully(null)
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val transactionsPaged: Flow<PagingData<TransactionListItem>> = combine(
        _searchQuery,
        _selectedTypes,
        _sortOrder,
        _dateRange
    ) { query, types, sort, range ->
        Quartet(query, types, sort, range)
    }.flatMapLatest { (query, types, sort, range) ->
        useCases.getTransactions(1L, query, types, sort, range.first, range.second)
    }.map { pagingData ->
        pagingData.map { TransactionListItem.TransactionItem(it) }
            .insertSeparators { before, after ->
                if (after == null) return@insertSeparators null
                
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val afterDate = sdf.format(after.transaction.date)
                
                if (before == null) {
                    return@insertSeparators TransactionListItem.DateHeader(afterDate)
                }
                
                val beforeDate = sdf.format(before.transaction.date)
                if (beforeDate != afterDate) {
                    TransactionListItem.DateHeader(afterDate)
                } else {
                    null
                }
            }
    }.cachedIn(viewModelScope)

    init {
        refreshTransactions()
    }


    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTypeFilterChanged(type: String) {
        viewModelScope.launch {
            if (type == "ALL") {
                preferencesRepository.updateSelectedTypes(emptySet())
                return@launch
            }
            
            val current = _selectedTypes.value
            val newTypes = if (current.contains(type)) {
                current - type
            } else {
                current + type
            }
            preferencesRepository.updateSelectedTypes(newTypes)
        }
    }

    fun onSortOrderChanged(order: SortOrder) {
        viewModelScope.launch {
            preferencesRepository.updateSortOrder(order)
        }
    }

    fun refreshTransactions() {
        viewModelScope.launch {
            _refreshStatus.value = RefreshStatus.Loading
            try {
                useCases.refreshTransactions(1L)
                _refreshStatus.value = RefreshStatus.Idle
                preferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
            } catch (e: Exception) {
                _refreshStatus.value = RefreshStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            useCases.syncPending()
            preferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
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
                syncStatus = SyncStatus.SAVED_OFFLINE
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

data class OperationalMetrics(
    val activeGroups: Int = 0,
    val todayMeetings: Int = 0,
    val pendingCollections: Int = 0,
    val offlineQueueCount: Int = 0,
    val syncingCount: Int = 0,
    val failedCount: Int = 0,
    val lastSyncTime: Long? = null
)

sealed interface OfflineStatus {
    data object Online : OfflineStatus
    data object OfflineActive : OfflineStatus
    data class PendingSync(val count: Int) : OfflineStatus
}

sealed interface SyncState {
    data object SavedOffline : SyncState
    data class PendingSync(val count: Int) : SyncState
    data class Syncing(val count: Int) : SyncState
    data class SyncedSuccessfully(val lastSyncTime: Long?) : SyncState
    data class RetryFailed(val count: Int) : SyncState
}

sealed class TransactionListItem {
    data class TransactionItem(val transaction: SavingsTransaction) : TransactionListItem()
    data class DateHeader(val date: String) : TransactionListItem()
}
