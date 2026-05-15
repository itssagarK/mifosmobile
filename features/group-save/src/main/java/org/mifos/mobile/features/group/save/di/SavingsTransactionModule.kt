package org.mifos.mobile.features.group.save.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobile.core.database.AppDatabase
import org.mifos.mobile.features.group.save.data.local.dao.MemberCollectionDao
import org.mifos.mobile.features.group.save.data.remote.api.SavingsTransactionApi
import org.mifos.mobile.features.group.save.data.repository.CollectionRepositoryImpl
import org.mifos.mobile.features.group.save.data.repository.SavingsTransactionRepositoryImpl
import org.mifos.mobile.features.group.save.domain.repository.CollectionRepository
import org.mifos.mobile.features.group.save.domain.repository.SavingsTransactionRepository
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

    @Binds
    @Singleton
    abstract fun bindCollectionRepository(
        impl: CollectionRepositoryImpl
    ): CollectionRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        impl: org.mifos.mobile.features.group.save.data.repository.GroupRepositoryImpl
    ): org.mifos.mobile.features.group.save.domain.repository.GroupRepository

    companion object {
        @Provides
        @Singleton
        fun provideSavingsTransactionApi(retrofit: Retrofit): SavingsTransactionApi {
            return retrofit.create(SavingsTransactionApi::class.java)
        }

        @Provides
        @Singleton
        fun provideMemberCollectionDao(database: AppDatabase): MemberCollectionDao {
            return database.memberCollectionDao()
        }
    }
}
