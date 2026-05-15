package org.mifos.mobile.features.group.save.domain.model

import java.util.Date

data class MeetingMember(
    val memberId: Long,
    val memberName: String,
    val isPresent: Boolean = false,
    val savingsContribution: Double = 0.0,
    val loanPayment: Double = 0.0,
    val syncStatus: SyncStatus = SyncStatus.SAVED_OFFLINE
)

data class MeetingWorkflow(
    val groupId: Long,
    val groupName: String,
    val date: Date = Date(),
    val members: List<MeetingMember> = emptyList(),
    val meetingNotes: String = "",
    val status: MeetingStatus = MeetingStatus.NOT_STARTED
)

enum class MeetingStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}
