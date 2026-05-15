package org.mifos.mobile.features.group.save.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.mifos.mobile.features.group.save.domain.model.CollectionStatus
import org.mifos.mobile.features.group.save.domain.model.Group
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.repository.GroupRepository
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor() : GroupRepository {

    private val mockGroups = listOf(
        Group(
            id = 1,
            name = "Unity VSLA Group",
            centerName = "North Center",
            memberCount = 25,
            collectionStatus = CollectionStatus.ACTIVE,
            pendingSyncCount = 0,
            nextMeetingDate = getFutureDate(2),
            syncStatus = SyncStatus.SYNCED
        ),
        Group(
            id = 2,
            name = "Sunshine Women Group",
            centerName = "West Center",
            memberCount = 18,
            collectionStatus = CollectionStatus.IN_PROGRESS,
            pendingSyncCount = 5,
            nextMeetingDate = Date(),
            syncStatus = SyncStatus.PENDING_SYNC
        ),
        Group(
            id = 3,
            name = "Progressive Farmers",
            centerName = "East Center",
            memberCount = 30,
            collectionStatus = CollectionStatus.COMPLETED,
            pendingSyncCount = 0,
            nextMeetingDate = getFutureDate(7),
            syncStatus = SyncStatus.SYNCED
        ),
        Group(
            id = 4,
            name = "Hope Savings Group",
            centerName = "North Center",
            memberCount = 22,
            collectionStatus = CollectionStatus.ACTIVE,
            pendingSyncCount = 12,
            nextMeetingDate = getFutureDate(1),
            syncStatus = SyncStatus.FAILED
        ),
        Group(
            id = 5,
            name = "Riverside VSLA",
            centerName = "South Center",
            memberCount = 20,
            collectionStatus = CollectionStatus.ACTIVE,
            pendingSyncCount = 0,
            nextMeetingDate = getFutureDate(3),
            syncStatus = SyncStatus.SYNCED
        )
    )

    override fun getGroups(): Flow<List<Group>> = flowOf(mockGroups)

    override fun getGroupsByCenter(centerName: String): Flow<List<Group>> = flowOf(
        mockGroups.filter { it.centerName == centerName }
    )

    override fun getGroupById(groupId: Long): Flow<Group?> = flowOf(
        mockGroups.find { it.id == groupId }
    )

    override suspend fun syncGroups() {
        delay(2000)
    }

    private fun getFutureDate(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }
}
