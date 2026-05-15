package org.mifos.mobile.features.group.save.domain.model

enum class SyncStatus {
    SAVED_OFFLINE,
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    FAILED
}
