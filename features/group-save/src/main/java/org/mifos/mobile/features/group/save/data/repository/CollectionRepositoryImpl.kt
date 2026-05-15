package org.mifos.mobile.features.group.save.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mifos.mobile.features.group.save.data.local.dao.MemberCollectionDao
import org.mifos.mobile.features.group.save.data.mapper.toDomain
import org.mifos.mobile.features.group.save.data.mapper.toEntity
import org.mifos.mobile.features.group.save.domain.model.MemberCollection
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.repository.CollectionRepository
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val dao: MemberCollectionDao
) : CollectionRepository {

    override fun getCollectionsByGroup(groupId: Long): Flow<List<MemberCollection>> {
        return dao.getCollectionsByGroup(groupId).map { entities ->
            entities.map { it.toDomain() }.ifEmpty {
                // Return default members if empty (mocking for field operations)
                listOf(
                    MemberCollection(101, "Grace Adama", groupId = groupId),
                    MemberCollection(102, "John Doe", groupId = groupId),
                    MemberCollection(103, "Mary Smith", groupId = groupId),
                    MemberCollection(104, "Kofi Mensah", groupId = groupId),
                    MemberCollection(105, "Sarah Jones", groupId = groupId)
                )
            }
        }
    }

    override fun getPendingCount(groupId: Long): Flow<Int> {
        return dao.getPendingCount(groupId)
    }

    override suspend fun saveCollections(collections: List<MemberCollection>) {
        dao.insertCollections(collections.map { 
            it.copy(syncStatus = SyncStatus.SAVED_OFFLINE).toEntity() 
        })
    }

    override suspend fun syncCollections(groupId: Long) {
        // Simulate network delay and sync process
        dao.updateGroupSyncStatus(groupId, SyncStatus.SYNCING.name)
        delay(3000) // Increased delay for realism
        
        try {
            // Simulate a 20% chance of failure for "Retry Failed" realism
            if (Math.random() < 0.2) {
                throw Exception("Network connectivity lost")
            }
            dao.updateGroupSyncStatus(groupId, SyncStatus.SYNCED.name)
        } catch (e: Exception) {
            dao.updateGroupSyncStatus(groupId, SyncStatus.FAILED.name)
        }
    }
}
