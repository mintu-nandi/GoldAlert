package com.example.goldalert.di

import com.example.goldalert.data.local.GoldAlertDatabase
import com.example.goldalert.data.local.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS implementation of [platformModule].
 *
 * Configures the iOS database builder and registers generated DAOs.
 */
actual fun platformModule(): Module = module {
    single {
        getDatabaseBuilder().build()
    }
    single { get<GoldAlertDatabase>().goldPriceDao() }
    single { get<GoldAlertDatabase>().thresholdDao() }
}
