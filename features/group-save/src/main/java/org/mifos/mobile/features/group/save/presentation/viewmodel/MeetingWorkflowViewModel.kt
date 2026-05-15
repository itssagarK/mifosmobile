package org.mifos.mobile.features.group.save.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.features.group.save.domain.model.*
import org.mifos.mobile.features.group.save.domain.repository.CollectionRepository
import org.mifos.mobile.features.group.save.domain.repository.GroupRepository
import javax.inject.Inject

@HiltViewModel
class MeetingWorkflowViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long = savedStateHandle.get<Long>("groupId") ?: 1L

    private val _meetingState = MutableStateFlow<MeetingWorkflow?>(null)
    val meetingState: StateFlow<MeetingWorkflow?> = _meetingState.asStateFlow()

    val totalMembersPresent: StateFlow<Int> = _meetingState.map { it?.members?.count { m -> m.isPresent } ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalContributions: StateFlow<Double> = _meetingState.map { 
        it?.members?.sumOf { m -> m.savingsContribution + m.loanPayment } ?: 0.0 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pendingSyncCount: StateFlow<Int> = _meetingState.map { 
        it?.members?.count { m -> m.syncStatus != SyncStatus.SYNCED && (m.savingsContribution > 0 || m.loanPayment > 0) } ?: 0 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            val group = groupRepository.getGroupById(groupId).firstOrNull()
            val initialCollections = collectionRepository.getCollectionsByGroup(groupId).firstOrNull() ?: emptyList()
            
            _meetingState.value = MeetingWorkflow(
                groupId = groupId,
                groupName = group?.name ?: "Unknown Group",
                members = initialCollections.map { 
                    MeetingMember(
                        memberId = it.memberId,
                        memberName = it.memberName,
                        isPresent = it.savingsDeposit > 0 || it.loanPayment > 0,
                        savingsContribution = it.savingsDeposit,
                        loanPayment = it.loanPayment,
                        syncStatus = it.syncStatus
                    )
                },
                status = MeetingStatus.IN_PROGRESS
            )
        }
    }

    fun updateAttendance(memberId: Long, isPresent: Boolean) {
        _meetingState.update { current ->
            current?.copy(
                members = current.members.map { 
                    if (it.memberId == memberId) it.copy(isPresent = isPresent) else it 
                }
            )
        }
    }

    fun updateContribution(memberId: Long, savings: Double? = null, loan: Double? = null) {
        _meetingState.update { current ->
            current?.copy(
                members = current.members.map { 
                    if (it.memberId == memberId) it.copy(
                        savingsContribution = savings ?: it.savingsContribution,
                        loanPayment = loan ?: it.loanPayment,
                        isPresent = true, // Auto-mark as present if contribution is added
                        syncStatus = SyncStatus.SAVED_OFFLINE
                    ) else it 
                }
            )
        }
    }

    fun updateNotes(notes: String) {
        _meetingState.update { it?.copy(meetingNotes = notes) }
    }

    fun saveMeetingLocally() {
        viewModelScope.launch {
            val members = _meetingState.value?.members ?: return@launch
            val collections = members.map { 
                MemberCollection(
                    memberId = it.memberId,
                    memberName = it.memberName,
                    savingsDeposit = it.savingsContribution,
                    loanPayment = it.loanPayment,
                    syncStatus = it.syncStatus,
                    groupId = groupId
                )
            }
            collectionRepository.saveCollections(collections)
        }
    }

    fun submitAndSync() {
        viewModelScope.launch {
            saveMeetingLocally()
            collectionRepository.syncCollections(groupId)
            _meetingState.update { it?.copy(status = MeetingStatus.COMPLETED) }
        }
    }
}
