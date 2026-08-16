package com.example.goldalert.di

import com.example.goldalert.data.local.GoldAlertDatabase
import com.example.goldalert.data.local.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android implementation of [platformModule].
 *
 * Injects the Android Context to configure the database builder and
 * registers the DAOs for Room KMP database.
 */
actual fun platformModule(): Module = module {
    single {
        // Injects Context via Koin (which is passed when initializing Koin)
        getDatabaseBuilder(context = get()).build()
    }
    single { get<GoldAlertDatabase>().goldPriceDao() }
    single { get<GoldAlertDatabase>().thresholdDao() }
}
