package org.mifos.mobile.features.group.save.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.group.save.domain.model.MemberCollection

interface CollectionRepository {
    fun getCollectionsByGroup(groupId: Long): Flow<List<MemberCollection>>
    fun getPendingCount(groupId: Long): Flow<Int>
    suspend fun saveCollections(collections: List<MemberCollection>)
    suspend fun syncCollections(groupId: Long)
}
