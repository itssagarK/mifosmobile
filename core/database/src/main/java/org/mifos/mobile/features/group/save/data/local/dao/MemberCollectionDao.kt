package org.mifos.mobile.features.group.save.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.mifos.mobile.features.group.save.data.local.entity.MemberCollectionEntity

@Dao
interface MemberCollectionDao {
    @Query("SELECT * FROM member_collections WHERE groupId = :groupId ORDER BY memberName ASC")
    fun getCollectionsByGroup(groupId: Long): Flow<List<MemberCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<MemberCollectionEntity>)

    @Query("UPDATE member_collections SET syncStatus = :status WHERE groupId = :groupId AND syncStatus != 'SYNCED'")
    suspend fun updateGroupSyncStatus(groupId: Long, status: String)

    @Query("DELETE FROM member_collections WHERE groupId = :groupId AND syncStatus = 'SYNCED'")
    suspend fun deleteSyncedCollections(groupId: Long)

    @Query("SELECT COUNT(*) FROM member_collections WHERE groupId = :groupId AND syncStatus != 'SYNCED'")
    fun getPendingCount(groupId: Long): Flow<Int>
}
