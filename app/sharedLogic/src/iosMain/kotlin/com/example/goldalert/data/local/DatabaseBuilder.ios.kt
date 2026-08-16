package com.example.goldalert.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.goldalert.data.local.encryption.EncryptedSQLiteDriver
import com.example.goldalert.data.local.encryption.IosKeyProvider
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * Builds a [RoomDatabase.Builder] for [GoldAlertDatabase] on iOS.
 *
 * The database file is stored in the app's Documents directory, which is
 * included in iCloud backups by default. To exclude from backup, mark the file
 * with `NSURLIsExcludedFromBackupKey` after creation.
 *
 * All data is encrypted at rest via [EncryptedSQLiteDriver] (SQLCipher) using
 * a passphrase managed by [IosKeyProvider] (iOS Keychain).
 *
 * Note: For SQLCipher PRAGMA key to take effect on iOS, the SQLCipher native
 * framework must be linked in the Xcode project (via CocoaPods: `pod 'SQLCipher'`).
 */
@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<GoldAlertDatabase> {
    val documentDirectory: String = NSFileManager.defaultManager
        .URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )?.path ?: error("Cannot resolve NSDocumentDirectory for database path")

    val dbPath = "$documentDirectory/gold_alert.db"

    return Room.databaseBuilder<GoldAlertDatabase>(name = dbPath)
        .setDriver(
            EncryptedSQLiteDriver(
                delegate = BundledSQLiteDriver(),
                keyProvider = IosKeyProvider()
            )
        )
}
