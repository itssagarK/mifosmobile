package org.mifos.mobile.features.group.save.data.mapper

import org.mifos.mobile.features.group.save.data.local.entity.MemberCollectionEntity
import org.mifos.mobile.features.group.save.data.local.entity.SavingsTransactionEntity
import org.mifos.mobile.features.group.save.data.remote.model.SavingsTransactionDto
import org.mifos.mobile.features.group.save.domain.model.MemberCollection
import org.mifos.mobile.features.group.save.domain.model.SavingsTransaction
import org.mifos.mobile.features.group.save.domain.model.SyncStatus
import org.mifos.mobile.features.group.save.domain.model.TransactionType
import java.util.Date

fun SavingsTransactionDto.toEntity(): SavingsTransactionEntity {
    return SavingsTransactionEntity(
        remoteId = id,
        amount = amount,
        date = date,
        type = type,
        accountId = accountId,
        description = description,
        syncStatus = "SYNCED"
    )
}

fun SavingsTransactionEntity.toDomain(): SavingsTransaction {
    return SavingsTransaction(
        id = remoteId ?: localId,
        amount = amount,
        date = Date(date),
        transactionType = try {
            TransactionType.valueOf(type)
        } catch (e: Exception) {
            TransactionType.UNKNOWN
        },
        accountId = accountId,
        description = description,
        syncStatus = try {
            SyncStatus.valueOf(syncStatus)
        } catch (e: Exception) {
            SyncStatus.SYNCED
        }
    )
}

fun SavingsTransaction.toEntity(): SavingsTransactionEntity {
    val isSynced = syncStatus == SyncStatus.SYNCED
    return SavingsTransactionEntity(
        localId = if (!isSynced) id else 0,
        remoteId = if (isSynced) id else null,
        amount = amount,
        date = date.time,
        type = transactionType.name,
        accountId = accountId,
        description = description,
        syncStatus = syncStatus.name
    )
}

fun SavingsTransaction.toDto(): SavingsTransactionDto {
    return SavingsTransactionDto(
        id = id,
        amount = amount,
        date = date.time,
        type = transactionType.name,
        accountId = accountId,
        description = description
    )
}

fun MemberCollection.toEntity(): MemberCollectionEntity {
    return MemberCollectionEntity(
        memberId = memberId,
        memberName = memberName,
        savingsDeposit = savingsDeposit,
        loanPayment = loanPayment,
        date = date.time,
        syncStatus = syncStatus.name,
        groupId = groupId
    )
}

fun MemberCollectionEntity.toDomain(): MemberCollection {
    return MemberCollection(
        memberId = memberId,
        memberName = memberName,
        savingsDeposit = savingsDeposit,
        loanPayment = loanPayment,
        date = Date(date),
        syncStatus = try {
            SyncStatus.valueOf(syncStatus)
        } catch (e: Exception) {
            SyncStatus.SAVED_OFFLINE
        },
        groupId = groupId
    )
}
