package org.mifos.mobile.features.savings.transaction.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_transactions")
data class SavingsTransactionEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Long?,
    val amount: Double,
    val date: Long,
    val type: String,
    val accountId: Long,
    val description: String?,
    val isPendingSync: Boolean = false
)
