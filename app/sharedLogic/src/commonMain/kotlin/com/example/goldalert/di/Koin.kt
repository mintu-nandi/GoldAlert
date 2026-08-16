package com.example.goldalert.di

import com.example.goldalert.data.network.GoldPriceApi
import com.example.goldalert.data.repository.GoldRepositoryImpl
import com.example.goldalert.domain.repository.GoldRepository
import com.example.goldalert.domain.usecase.AddThresholdUseCase
import com.example.goldalert.domain.usecase.DeleteThresholdUseCase
import com.example.goldalert.domain.usecase.EvaluateThresholdsUseCase
import com.example.goldalert.domain.usecase.ObserveAllThresholdsUseCase
import com.example.goldalert.domain.usecase.SyncGoldPriceUseCase
import com.example.goldalert.domain.usecase.ToggleThresholdUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Shared Koin module containing platform-independent dependency declarations.
 *
 * Configures the Ktor Client, serialization, remote API interfaces, repositories,
 * and Domain UseCases.
 *
 * @param baseUrl Base URL for the Ktor gold price microservice.
 * @param triggerNotification Platform-specific local notification trigger callback.
 */
fun commonModule(
    baseUrl: String,
    triggerNotification: (title: String, body: String) -> Unit
) = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    coerceInputValues = true
                })
            }
        }
    }

    single { GoldPriceApi(client = get(), baseUrl = baseUrl) }

    single<GoldRepository> {
        GoldRepositoryImpl(
            priceDao = get(),
            thresholdDao = get(),
            priceApi = get(),
            ioDispatcher = Dispatchers.Default
        )
    }

    // UseCases
    single { ObserveAllThresholdsUseCase(repository = get()) }
    single { AddThresholdUseCase(repository = get()) }
    single { ToggleThresholdUseCase(repository = get()) }
    single { DeleteThresholdUseCase(repository = get()) }
    single { EvaluateThresholdsUseCase(repository = get(), triggerNotification = triggerNotification) }
    single { SyncGoldPriceUseCase(repository = get(), evaluateThresholdsUseCase = get()) }

    // ViewModels
    factory { com.example.goldalert.presentation.DashboardViewModel(repository = get(), syncGoldPriceUseCase = get()) }
    factory { com.example.goldalert.presentation.ThresholdListViewModel(observeAllThresholdsUseCase = get(), addThresholdUseCase = get(), toggleThresholdUseCase = get(), deleteThresholdUseCase = get()) }
    factory { com.example.goldalert.presentation.CandleChartViewModel(repository = get()) }
}

/**
 * Platform-specific module supplying target database engines and KeyProviders.
 */
expect fun platformModule(): Module
