package com.example.goldalert.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.goldalert.data.local.encryption.AndroidKeyProvider
import com.example.goldalert.data.local.encryption.EncryptedSQLiteDriver

/**
 * Builds a [RoomDatabase.Builder] for [GoldAlertDatabase] on Android.
 *
 * The database file is stored in the app's private databases directory.
 * All data is encrypted at rest via [EncryptedSQLiteDriver] (SQLCipher) using
 * a passphrase managed by [AndroidKeyProvider].
 *
 * Call `.build()` on the returned builder to obtain the [GoldAlertDatabase] singleton.
 * This function is invoked once inside the Koin DI module (Step 3).
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<GoldAlertDatabase> {
    val dbFile = context.getDatabasePath("gold_alert.db")
    return Room.databaseBuilder<GoldAlertDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    ).setDriver(
        EncryptedSQLiteDriver(
            delegate = BundledSQLiteDriver(),
            keyProvider = AndroidKeyProvider(context)
        )
    )
}
