package org.mifos.mobile.features.group.save.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.core.common.NetworkMonitor
import org.mifos.mobile.features.group.save.data.repository.UserPreferencesRepository
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
import javax.inject.Inject

data class SyncStatusUiState(
    val pendingQueue: List<SavingsTransaction> = emptyList(),
    val syncedOperations: List<SavingsTransaction> = emptyList(),
    val failedOperations: List<SavingsTransaction> = emptyList(),
    val lastSyncTimestamp: Long = 0L,
    val isSyncing: Boolean = false,
    val isOnline: Boolean = false
)

@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    private val repository: SavingsTransactionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)

    val uiState: StateFlow<SyncStatusUiState> = combine(
        repository.getAllTransactions(),
        userPreferencesRepository.userPreferencesFlow,
        networkMonitor.isOnline,
        _isSyncing
    ) { transactions, preferences, online, syncing ->
        SyncStatusUiState(
            pendingQueue = transactions.filter { it.syncStatus == SyncStatus.PENDING_SYNC || it.syncStatus == SyncStatus.SYNCING },
            syncedOperations = transactions.filter { it.syncStatus == SyncStatus.SYNCED }.take(20), // Show last 20 synced
            failedOperations = transactions.filter { it.syncStatus == SyncStatus.FAILED },
            lastSyncTimestamp = preferences.lastSyncTimestamp,
            isSyncing = syncing,
            isOnline = online
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SyncStatusUiState()
    )

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncPendingTransactions()
                userPreferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
            } catch (e: Exception) {
                // Error handled by repository status updates
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun retryFailed(transaction: SavingsTransaction) {
        viewModelScope.launch {
            // In a real app, we might just mark it as PENDING_SYNC again
            // and let the sync worker or syncNow handle it.
            // For now, let's just trigger a full sync.
            syncNow()
        }
    }
}
