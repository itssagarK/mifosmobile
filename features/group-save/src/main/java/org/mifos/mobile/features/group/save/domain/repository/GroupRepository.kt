package org.mifos.mobile.features.group.save.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.group.save.domain.model.Group

interface GroupRepository {
    fun getGroups(): Flow<List<Group>>
    fun getGroupsByCenter(centerName: String): Flow<List<Group>>
    fun getGroupById(groupId: Long): Flow<Group?>
    suspend fun syncGroups()
}
