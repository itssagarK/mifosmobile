package org.mifos.mobile.features.group.save.domain.model

import java.util.Date

data class MemberCollection(
    val memberId: Long,
    val memberName: String,
    val savingsDeposit: Double = 0.0,
    val loanPayment: Double = 0.0,
    val date: Date = Date(),
    val syncStatus: SyncStatus = SyncStatus.SAVED_OFFLINE,
    val groupId: Long = 1L
)
