package com.example.goldalert.domain.repository

import com.example.goldalert.data.local.entity.GoldPriceEntity
import com.example.goldalert.data.local.entity.ThresholdEntity
import kotlinx.coroutines.flow.Flow

/**
 * Domain Repository interface for the GoldAlert application.
 *
 * Adheres strictly to the Reactive Data Layer Architecture (RDLA):
 *  - State updates are observed via cold/hot Flows from the local database.
 *  - Data writes are suspend functions (called from Domain UseCases).
 *  - Fetch operations do NOT return the network payload to the caller;
 *    they write to the database which serves as the Single Source of Truth (SSOT).
 */
interface GoldRepository {

    // --- Gold Price operations ---

    /**
     * Reactively observes the single most-recent price in the database.
     * Emits null if no prices have been fetched yet.
     */
    fun observeLatestPrice(): Flow<GoldPriceEntity?>

    /**
     * Reactively observes the full historical log of gold prices.
     */
    fun observePriceHistory(): Flow<List<GoldPriceEntity>>

    /**
     * Fetches the latest price from the network microservice, maps it, and
     * writes it to the local encrypted Room database.
     *
     * Following the SSOT rule, this returns a Result<Unit> indicating success
     * or failure, rather than returning the parsed network data.
     */
    suspend fun fetchAndSaveLatestPrice(): Result<Unit>


    // --- Threshold Alert operations ---

    /**
     * Observes all user-defined alert thresholds (both active and paused).
     */
    fun observeAllThresholds(): Flow<List<ThresholdEntity>>

    /**
     * Observes only active thresholds (used by background task evaluator).
     */
    fun observeActiveThresholds(): Flow<List<ThresholdEntity>>

    /**
     * Creates and inserts a new price threshold. Returns the row ID of the new entity.
     *
     * @param price     Alert price in USD/oz.
     * @param direction Trigger direction: "ABOVE" or "BELOW".
     * @param label     Optional user-defined note.
     */
    suspend fun addThreshold(price: Double, direction: String, label: String): Long

    /**
     * Persists updates to an existing threshold.
     */
    suspend fun updateThreshold(threshold: ThresholdEntity)

    /**
     * Deletes a threshold from the database.
     */
    suspend fun deleteThreshold(threshold: ThresholdEntity)

    /**
     * Deletes a threshold by its unique primary key.
     */
    suspend fun deleteThresholdById(id: Long)
}
