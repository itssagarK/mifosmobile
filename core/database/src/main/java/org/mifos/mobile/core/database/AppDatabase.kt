package org.mifos.mobile.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.mifos.mobile.features.savings.transaction.data.local.dao.SavingsTransactionDao
import org.mifos.mobile.features.savings.transaction.data.local.entity.SavingsTransactionEntity

@Database(
    entities = [SavingsTransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savingsTransactionDao(): SavingsTransactionDao
}
