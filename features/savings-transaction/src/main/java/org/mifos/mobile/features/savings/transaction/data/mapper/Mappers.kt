package org.mifos.mobile.features.savings.transaction.data.mapper

import org.mifos.mobile.features.savings.transaction.data.local.entity.SavingsTransactionEntity
import org.mifos.mobile.features.savings.transaction.data.remote.model.SavingsTransactionDto
import org.mifos.mobile.features.savings.transaction.domain.model.SavingsTransaction
import org.mifos.mobile.features.savings.transaction.domain.model.TransactionType
import java.util.Date

fun SavingsTransactionDto.toEntity(): SavingsTransactionEntity {
    return SavingsTransactionEntity(
        remoteId = id,
        amount = amount,
        date = date,
        type = type,
        accountId = accountId,
        description = description,
        isPendingSync = false
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
        isPendingSync = isPendingSync
    )
}

fun SavingsTransaction.toEntity(): SavingsTransactionEntity {
    return SavingsTransactionEntity(
        localId = if (isPendingSync) id else 0,
        remoteId = if (isPendingSync) null else id,
        amount = amount,
        date = date.time,
        type = transactionType.name,
        accountId = accountId,
        description = description,
        isPendingSync = isPendingSync
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
