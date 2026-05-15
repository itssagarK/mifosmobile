package org.mifos.mobile.features.group.save.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.group.save.domain.model.Group
import org.mifos.mobile.features.group.save.domain.model.MemberCollection
import org.mifos.mobile.features.group.save.domain.repository.CollectionRepository
import org.mifos.mobile.features.group.save.domain.repository.GroupRepository
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long = savedStateHandle.get<Long>("groupId") ?: 1L

    val groupState: StateFlow<UiState<Group>> = groupRepository.getGroupById(groupId)
        .map { group ->
            if (group != null) UiState.Success(group) else UiState.Error("Group not found")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val membersState: StateFlow<UiState<List<MemberCollection>>> = collectionRepository.getCollectionsByGroup(groupId)
        .map { list ->
            if (list.isEmpty()) UiState.Empty else UiState.Success(list)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val collectionProgress: StateFlow<Float> = membersState.map { state ->
        if (state is UiState.Success) {
            val total = state.data.size
            val processed = state.data.count { it.savingsDeposit > 0 || it.loanPayment > 0 }
            if (total > 0) processed.toFloat() / total else 0f
        } else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val pendingCollectionsCount: StateFlow<Int> = membersState.map { state ->
        if (state is UiState.Success) {
            state.data.count { it.savingsDeposit > 0 || it.loanPayment > 0 }
        } else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun retrySync() {
        viewModelScope.launch {
            collectionRepository.syncCollections(groupId)
        }
    }
}
