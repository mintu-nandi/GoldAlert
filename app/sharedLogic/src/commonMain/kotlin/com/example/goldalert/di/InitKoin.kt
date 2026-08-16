package com.example.goldalert.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Entry point to start the Koin Dependency Injection framework.
 *
 * @param baseUrl             Baseline URL for Ktor price API. Defaults to live gold-api.com feed.
 * @param triggerNotification Local notification callback for alerting users.
 * @param appDeclaration      Optional configuration closure for platform initialization (e.g. passing Android Context).
 */
fun initKoin(
    baseUrl: String = "https://api.gold-api.com/price/XAU/USD",
    triggerNotification: (title: String, body: String) -> Unit,
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(
        commonModule(baseUrl, triggerNotification),
        platformModule()
    )
}

fun initKoinIos(
    triggerNotification: (title: String, body: String) -> Unit
) = initKoin(
    baseUrl = "https://api.gold-api.com/price/XAU/USD",
    triggerNotification = triggerNotification
)

class KoinHelper : org.koin.core.component.KoinComponent {
    fun getDashboardViewModel(): com.example.goldalert.presentation.DashboardViewModel = get()
    fun getThresholdListViewModel(): com.example.goldalert.presentation.ThresholdListViewModel = get()
    fun getCandleChartViewModel(): com.example.goldalert.presentation.CandleChartViewModel = get()
}

