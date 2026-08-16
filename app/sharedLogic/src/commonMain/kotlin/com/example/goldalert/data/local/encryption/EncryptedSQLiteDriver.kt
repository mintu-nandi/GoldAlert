package com.example.goldalert.data.local.encryption

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver

/**
 * Decorates any [SQLiteDriver] with SQLCipher at-rest encryption.
 *
 * How it works:
 *  1. The delegate driver opens the raw SQLite/SQLCipher file.
 *  2. **Immediately** (before any other SQL) we execute `PRAGMA key`.
 *     This is a strict SQLCipher requirement — any earlier statement will
 *     fail because the file content is still encrypted.
 *  3. The now-unlocked connection is returned to Room for normal use.
 *
 * Usage (platform database builder):
 * ```kotlin
 * Room.databaseBuilder<GoldAlertDatabase>(name = path)
 *     .setDriver(EncryptedSQLiteDriver(BundledSQLiteDriver(), keyProvider))
 *     .build()
 * ```
 *
 * @param delegate  The underlying SQLiteDriver (typically [BundledSQLiteDriver]).
 * @param keyProvider  Provides the passphrase from the platform's secure store.
 */
class EncryptedSQLiteDriver(
    private val delegate: SQLiteDriver,
    private val keyProvider: KeyProvider
) : SQLiteDriver {

    override fun open(fileName: String): SQLiteConnection {
        val connection = delegate.open(fileName)
        val passphrase = keyProvider.getOrCreateDatabaseKey()
        // CRITICAL: PRAGMA key must be the very first statement.
        val statement = connection.prepare("PRAGMA key = '$passphrase';")
        try {
            statement.step()
        } finally {
            statement.close()
        }
        return connection
    }
}
