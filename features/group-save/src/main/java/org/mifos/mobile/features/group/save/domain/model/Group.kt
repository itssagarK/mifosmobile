package org.mifos.mobile.features.group.save.domain.model

import java.util.Date

data class Group(
    val id: Long,
    val name: String,
    val centerName: String,
    val memberCount: Int,
    val collectionStatus: CollectionStatus,
    val pendingSyncCount: Int,
    val nextMeetingDate: Date,
    val lastCollectionDate: Date? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

enum class CollectionStatus {
    ACTIVE,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
