package com.example.goldalert.domain.usecase

import com.example.goldalert.data.local.entity.ThresholdEntity
import com.example.goldalert.domain.repository.GoldRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock

/**
 * Checks all active alert thresholds against the latest gold price.
 *
 * If the current price crosses a threshold target:
 *  1. Fires a local notification.
 *  2. Deactivates the threshold (or updates its lastTriggeredAt timestamp) to prevent spam.
 */
class EvaluateThresholdsUseCase(
    private val repository: GoldRepository,
    private val triggerNotification: (title: String, body: String) -> Unit
) {
    suspend operator fun invoke() {
        val latestPriceEntity = repository.observeLatestPrice().firstOrNull() ?: return
        val currentPrice = latestPriceEntity.pricePerOunce

        val activeThresholds = repository.observeActiveThresholds().firstOrNull() ?: return

        for (threshold in activeThresholds) {
            val isTriggered = when (threshold.direction) {
                "ABOVE" -> currentPrice >= threshold.targetPrice
                "BELOW" -> currentPrice <= threshold.targetPrice
                else -> false
            }

            if (isTriggered && shouldTriggerAlert(threshold)) {
                val labelText = if (threshold.label.isNotEmpty()) " - ${threshold.label}" else ""

                // Fire notification callback
                triggerNotification(
                    "Gold Alert!",
                    "Gold price has reached your threshold limit: $${threshold.targetPrice}$labelText"
                )

                // Update database (deactivate threshold and set triggered timestamp)
                repository.updateThreshold(
                    threshold.copy(
                        lastTriggeredAt = Clock.System.now().toEpochMilliseconds(),
                        isActive = false // Deactivate after single trigger to avoid alert loops
                    )
                )
            }
        }
    }

    private fun shouldTriggerAlert(threshold: ThresholdEntity): Boolean {
        val lastTriggered = threshold.lastTriggeredAt ?: return true
        val fifteenMinutesMillis = 15 * 60 * 1000
        return (Clock.System.now().toEpochMilliseconds() - lastTriggered) > fifteenMinutesMillis
    }
}
