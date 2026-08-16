package com.example.goldalert.data.local.encryption

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUUID
import platform.Foundation.NSString
import platform.Foundation.dataUsingEncoding
import platform.Foundation.CFBridgingRetain
import platform.Foundation.CFBridgingRelease
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * iOS implementation of [KeyProvider].
 *
 * Stores the database encryption passphrase in the iOS Keychain under a
 * Generic Password item. The item is accessible after the first device unlock,
 * which allows background tasks (BGTaskScheduler) to open the database even
 * when the screen is locked.
 *
 * On first launch a new UUID passphrase is generated and stored in Keychain.
 * The passphrase survives app reinstalls (Keychain items persist unless explicitly
 * deleted or the device is erased / restored without iCloud Keychain).
 */
class IosKeyProvider : KeyProvider {

    companion object {
        private const val SERVICE = "com.example.goldalert.db"
        private const val ACCOUNT = "db_passphrase_v1"
    }

    override fun getOrCreateDatabaseKey(): String =
        readFromKeychain() ?: generateAndStore()

    @OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
    private fun readFromKeychain(): String? = memScoped {
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 5, null, null)!!
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        
        val serviceRef = CFBridgingRetain(NSString.create(string = SERVICE))
        val accountRef = CFBridgingRetain(NSString.create(string = ACCOUNT))
        
        CFDictionaryAddValue(query, kSecAttrService, serviceRef)
        CFDictionaryAddValue(query, kSecAttrAccount, accountRef)
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)
        
        CFRelease(serviceRef)
        CFRelease(accountRef)
        CFRelease(query)

        if (status == errSecSuccess) {
            val data = CFBridgingRelease(result.value) as? NSData
            if (data != null) {
                val nsString = NSString.create(data = data, encoding = NSUTF8StringEncoding)
                return nsString?.toString()
            }
        }
        null
    }

    @OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
    private fun generateAndStore(): String {
        val key = NSUUID().UUIDString
        val keyData = NSString.create(string = key).dataUsingEncoding(NSUTF8StringEncoding)!!

        val attrs = CFDictionaryCreateMutable(kCFAllocatorDefault, 5, null, null)!!
        CFDictionaryAddValue(attrs, kSecClass, kSecClassGenericPassword)
        
        val serviceRef = CFBridgingRetain(NSString.create(string = SERVICE))
        val accountRef = CFBridgingRetain(NSString.create(string = ACCOUNT))
        val dataRef = CFBridgingRetain(keyData)

        CFDictionaryAddValue(attrs, kSecAttrService, serviceRef)
        CFDictionaryAddValue(attrs, kSecAttrAccount, accountRef)
        CFDictionaryAddValue(attrs, kSecValueData, dataRef)
        CFDictionaryAddValue(attrs, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)

        SecItemAdd(attrs, null)
        
        CFRelease(serviceRef)
        CFRelease(accountRef)
        CFRelease(dataRef)
        CFRelease(attrs)
        
        return key
    }
}
