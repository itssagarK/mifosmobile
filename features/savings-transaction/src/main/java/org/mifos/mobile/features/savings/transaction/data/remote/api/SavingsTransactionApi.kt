package org.mifos.mobile.features.savings.transaction.data.remote.api

import org.mifos.mobile.features.savings.transaction.data.remote.model.SavingsTransactionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SavingsTransactionApi {
    @GET("savingsaccounts/{accountId}/transactions")
    suspend fun getTransactions(@Path("accountId") accountId: Long): List<SavingsTransactionDto>

    @POST("savingsaccounts/{accountId}/transactions")
    suspend fun createTransaction(
        @Path("accountId") accountId: Long,
        @Body transaction: SavingsTransactionDto
    ): SavingsTransactionDto
}
