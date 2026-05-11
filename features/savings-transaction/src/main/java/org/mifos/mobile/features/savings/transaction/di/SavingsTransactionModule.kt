package org.mifos.mobile.features.savings.transaction.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobile.features.savings.transaction.data.remote.api.SavingsTransactionApi
import org.mifos.mobile.features.savings.transaction.data.repository.SavingsTransactionRepositoryImpl
import org.mifos.mobile.features.savings.transaction.domain.repository.SavingsTransactionRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SavingsTransactionModule {

    @Binds
    @Singleton
    abstract fun bindSavingsTransactionRepository(
        impl: SavingsTransactionRepositoryImpl
    ): SavingsTransactionRepository

    companion object {
        @Provides
        @Singleton
        fun provideSavingsTransactionApi(retrofit: Retrofit): SavingsTransactionApi {
            return retrofit.create(SavingsTransactionApi::class.java)
        }
    }
}
