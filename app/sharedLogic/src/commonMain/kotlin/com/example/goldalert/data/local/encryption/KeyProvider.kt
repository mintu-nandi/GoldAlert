package com.example.goldalert.data.local.encryption

/**
 * Platform-agnostic contract for securely providing the database encryption passphrase.
 *
 * Platform implementations:
 *  - Android → [AndroidKeyProvider] (Android SharedPreferences, upgradeable to Keystore)
 *  - iOS     → [IosKeyProvider] (iOS Keychain via Security framework)
 *
 * The passphrase is used by [EncryptedSQLiteDriver] as the SQLCipher PRAGMA key.
 * It is never logged, transmitted, or stored in plain-text source code.
 */
interface KeyProvider {
    /**
     * Returns the current database encryption passphrase.
     * If no passphrase exists yet, generates a new one, persists it
     * in the platform's secure store, and returns it.
     *
     * This method is called once per database open — keep it fast.
     */
    fun getOrCreateDatabaseKey(): String
}
