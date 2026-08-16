package com.example.goldalert.domain.usecase

import com.example.goldalert.domain.repository.GoldRepository

/**
 * Triggers a gold price synchronization from the remote server microservice.
 *
 * Adhering to the SSOT (Single Source of Truth) architecture, this fetches
 * the gold price, stores it in the local database, and then runs the
 * threshold evaluation engine.
 */
class SyncGoldPriceUseCase(
    private val repository: GoldRepository,
    private val evaluateThresholdsUseCase: EvaluateThresholdsUseCase
) {
    suspend operator fun invoke(): Result<Unit> {
        val result = repository.fetchAndSaveLatestPrice()
        if (result.isSuccess) {
            // Immediately run threshold evaluation against the new price
            evaluateThresholdsUseCase()
        }
        return result
    }
}
