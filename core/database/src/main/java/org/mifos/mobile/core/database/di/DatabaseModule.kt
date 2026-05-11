package org.mifos.mobile.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifos.mobile.core.database.AppDatabase
import org.mifos.mobile.features.savings.transaction.data.local.dao.SavingsTransactionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mifos_mobile.db"
        ).build()
    }

    @Provides
    fun provideSavingsTransactionDao(database: AppDatabase): SavingsTransactionDao {
        return database.savingsTransactionDao()
    }
}
