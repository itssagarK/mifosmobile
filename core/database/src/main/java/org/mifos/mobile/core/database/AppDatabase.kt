package org.mifos.mobile.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.mifos.mobile.features.group.save.data.local.dao.MemberCollectionDao
import org.mifos.mobile.features.group.save.data.local.dao.SavingsTransactionDao
import org.mifos.mobile.features.group.save.data.local.entity.MemberCollectionEntity
import org.mifos.mobile.features.group.save.data.local.entity.SavingsTransactionEntity

@Database(
    entities = [
        SavingsTransactionEntity::class,
        MemberCollectionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savingsTransactionDao(): SavingsTransactionDao
    abstract fun memberCollectionDao(): MemberCollectionDao
}
