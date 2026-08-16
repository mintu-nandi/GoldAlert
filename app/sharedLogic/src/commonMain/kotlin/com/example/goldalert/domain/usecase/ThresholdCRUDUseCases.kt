package com.example.goldalert.domain.usecase

import com.example.goldalert.data.local.entity.ThresholdEntity
import com.example.goldalert.domain.repository.GoldRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes all user-defined alert thresholds (both active and paused).
 */
class ObserveAllThresholdsUseCase(private val repository: GoldRepository) {
    operator fun invoke(): Flow<List<ThresholdEntity>> = repository.observeAllThresholds()
}

/**
 * Creates and inserts a new price alert threshold.
 */
class AddThresholdUseCase(private val repository: GoldRepository) {
    suspend operator fun invoke(price: Double, direction: String, label: String = ""): Long =
        repository.addThreshold(price, direction, label)
}

/**
 * Toggles the active/paused state of a threshold.
 */
class ToggleThresholdUseCase(private val repository: GoldRepository) {
    suspend operator fun invoke(threshold: ThresholdEntity) {
        val updated = threshold.copy(isActive = !threshold.isActive)
        repository.updateThreshold(updated)
    }
}

/**
 * Deletes a threshold from the database.
 */
class DeleteThresholdUseCase(private val repository: GoldRepository) {
    suspend operator fun invoke(threshold: ThresholdEntity) =
        repository.deleteThreshold(threshold)

    suspend operator fun invoke(id: Long) =
        repository.deleteThresholdById(id)
}
