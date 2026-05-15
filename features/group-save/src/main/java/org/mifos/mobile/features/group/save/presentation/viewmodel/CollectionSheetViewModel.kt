package org.mifos.mobile.features.group.save.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.features.group.save.domain.model.MemberCollection
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.repository.CollectionRepository
import javax.inject.Inject

@HiltViewModel
class CollectionSheetViewModel @Inject constructor(
    private val repository: CollectionRepository
) : ViewModel() {

    private val _groupId = MutableStateFlow(1L)
    val groupId: StateFlow<Long> = _groupId.asStateFlow()

    private val _collections = MutableStateFlow<List<MemberCollection>>(emptyList())
    val collections: StateFlow<List<MemberCollection>> = _collections.asStateFlow()

    val pendingCount: StateFlow<Int> = _collections.map { list ->
        list.count { it.syncStatus != SyncStatus.SYNCED && (it.savingsDeposit > 0 || it.loanPayment > 0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCollected: StateFlow<Double> = _collections.map { list ->
        list.sumOf { it.savingsDeposit + it.loanPayment }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val membersProcessed: StateFlow<Int> = _collections.map { list ->
        list.count { it.savingsDeposit > 0 || it.loanPayment > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            _groupId.flatMapLatest { id ->
                repository.getCollectionsByGroup(id)
            }.collect { list ->
                _collections.value = list
            }
        }
    }

    fun updateCollection(memberId: Long, savings: Double? = null, loan: Double? = null) {
        val currentList = _collections.value.toMutableList()
        val index = currentList.indexOfFirst { it.memberId == memberId }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(
                savingsDeposit = savings ?: item.savingsDeposit,
                loanPayment = loan ?: item.loanPayment,
                syncStatus = SyncStatus.SAVED_OFFLINE // Mark as modified locally
            )
            _collections.value = currentList
        }
    }

    fun saveOffline() {
        viewModelScope.launch {
            repository.saveCollections(_collections.value)
        }
    }

    fun submitCollections() {
        viewModelScope.launch {
            repository.saveCollections(_collections.value)
            repository.syncCollections(_groupId.value)
        }
    }

    fun retrySync() {
        viewModelScope.launch {
            repository.syncCollections(_groupId.value)
        }
    }
}
