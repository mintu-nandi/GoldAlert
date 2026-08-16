package com.example.goldalert.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.ConstructedBy
import com.example.goldalert.data.local.dao.GoldPriceDao
import com.example.goldalert.data.local.dao.ThresholdDao
import com.example.goldalert.data.local.entity.GoldPriceEntity
import com.example.goldalert.data.local.entity.ThresholdEntity

/**
 * The single Room database for GoldAlert.
 *
 * - Encrypted at rest via [EncryptedSQLiteDriver] (SQLCipher).
 * - Version increments require a [androidx.room.migration.Migration] to preserve data.
 * - Obtain the singleton via the Koin DI graph — never instantiate directly.
 */
@Database(
    entities = [
        GoldPriceEntity::class,
        ThresholdEntity::class
    ],
    version = 1,
    exportSchema = true // Outputs schema JSON to /schemas for version-control & CI checks
)
@ConstructedBy(GoldAlertDatabaseConstructor::class)
abstract class GoldAlertDatabase : RoomDatabase() {
    abstract fun goldPriceDao(): GoldPriceDao
    abstract fun thresholdDao(): ThresholdDao
}

/**
 * Required by the Room KMP annotation processor to generate the
 * platform-specific [GoldAlertDatabase] implementation.
 *
 * The `@Suppress` is intentional: the actual implementations are generated
 * by KSP at compile time for each platform target.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object GoldAlertDatabaseConstructor : RoomDatabaseConstructor<GoldAlertDatabase>
