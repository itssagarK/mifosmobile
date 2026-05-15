package org.mifos.mobile.features.group.save.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_collections")
data class MemberCollectionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val memberId: Long,
    val memberName: String,
    val savingsDeposit: Double,
    val loanPayment: Double,
    val date: Long,
    val syncStatus: String,
    val groupId: Long
)
