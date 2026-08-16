package com.example.goldalert.data.local.encryption

import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation of [KeyProvider].
 *
 * Stores the database encryption passphrase in [SharedPreferences].
 * On first launch, a new UUID passphrase is generated and persisted.
 *
 * Security note: This is a baseline implementation. For production hardening,
 * replace with [androidx.security.crypto.EncryptedSharedPreferences] backed by
 * the Android Keystore, which protects the passphrase with hardware-backed keys.
 *
 * The key is tied to the device — if the user uninstalls the app or clears data,
 * the key is lost and the database cannot be opened (data is effectively deleted).
 */
class AndroidKeyProvider(private val context: Context) : KeyProvider {

    companion object {
        private const val PREFS_NAME = "gold_alert_secure_prefs"
        private const val KEY_DB_PASSPHRASE = "db_passphrase_v1"
    }

    override fun getOrCreateDatabaseKey(): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        return prefs.getString(KEY_DB_PASSPHRASE, null) ?: generateAndStore(prefs)
    }

    private fun generateAndStore(prefs: SharedPreferences): String {
        val newKey = java.util.UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DB_PASSPHRASE, newKey).apply()
        return newKey
    }
}
