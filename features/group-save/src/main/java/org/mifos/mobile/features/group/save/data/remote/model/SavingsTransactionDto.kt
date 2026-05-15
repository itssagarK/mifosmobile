package org.mifos.mobile.features.group.save.data.remote.model

import com.google.gson.annotations.SerializedName

data class SavingsTransactionDto(
    @SerializedName("id") val id: Long,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: Long, // Using timestamp for simplicity in DTO
    @SerializedName("type") val type: String,
    @SerializedName("accountId") val accountId: Long,
    @SerializedName("description") val description: String?
)
