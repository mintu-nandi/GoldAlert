package com.example.goldalert.domain.usecase

import com.example.goldalert.data.local.entity.GoldPriceEntity
import com.example.goldalert.data.local.entity.ThresholdEntity
import com.example.goldalert.domain.repository.GoldRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EvaluateThresholdsUseCaseTest {

    private class FakeGoldRepository : GoldRepository {
        val latestPriceFlow = MutableStateFlow<GoldPriceEntity?>(null)
        val activeThresholdsFlow = MutableStateFlow<List<ThresholdEntity>>(emptyList())
        val updatedThresholds = mutableListOf<ThresholdEntity>()

        override fun observeLatestPrice(): Flow<GoldPriceEntity?> = latestPriceFlow

        override fun observePriceHistory(): Flow<List<GoldPriceEntity>> {
            throw NotImplementedError()
        }

        override suspend fun fetchAndSaveLatestPrice(): Result<Unit> {
            throw NotImplementedError()
        }

        override fun observeAllThresholds(): Flow<List<ThresholdEntity>> {
            throw NotImplementedError()
        }

        override fun observeActiveThresholds(): Flow<List<ThresholdEntity>> = activeThresholdsFlow

        override suspend fun addThreshold(price: Double, direction: String, label: String): Long {
            throw NotImplementedError()
        }

        override suspend fun updateThreshold(threshold: ThresholdEntity) {
            updatedThresholds.add(threshold)
        }

        override suspend fun deleteThreshold(threshold: ThresholdEntity) {
            throw NotImplementedError()
        }

        override suspend fun deleteThresholdById(id: Long) {
            throw NotImplementedError()
        }
    }

    @Test
    fun testAlertIsTriggeredWhenPriceCrossesAbove() = runBlocking {
        val repository = FakeGoldRepository()
        var triggeredTitle: String? = null
        var triggeredBody: String? = null

        val useCase = EvaluateThresholdsUseCase(repository) { title, body ->
            triggeredTitle = title
            triggeredBody = body
        }

        // Set up active threshold: ABOVE $2000
        repository.activeThresholdsFlow.value = listOf(
            ThresholdEntity(
                id = 1,
                targetPrice = 2000.0,
                direction = "ABOVE",
                label = "Target met",
                isActive = true,
                createdAt = 0,
                lastTriggeredAt = null
            )
        )

        // Set up current price: $2010 (rose above $2000)
        repository.latestPriceFlow.value = GoldPriceEntity(
            id = 1,
            pricePerOunce = 2010.0,
            currency = "USD",
            timestamp = 1000,
            source = "test"
        )

        // Execute use case
        useCase()

        // Assertions
        assertNotNull(triggeredTitle)
        assertEquals("Gold Alert!", triggeredTitle)
        assertTrue(triggeredBody!!.contains("rose above $2000.0"))
        assertTrue(triggeredBody!!.contains("Current: $2010.0"))
        
        // Assert database is updated (deactivated)
        assertEquals(1, repository.updatedThresholds.size)
        assertFalse(repository.updatedThresholds[0].isActive)
    }

    @Test
    fun testAlertIsTriggeredWhenPriceDropsBelow() = runBlocking {
        val repository = FakeGoldRepository()
        var triggeredTitle: String? = null
        var triggeredBody: String? = null

        val useCase = EvaluateThresholdsUseCase(repository) { title, body ->
            triggeredTitle = title
            triggeredBody = body
        }

        // Set up active threshold: BELOW $1900
        repository.activeThresholdsFlow.value = listOf(
            ThresholdEntity(
                id = 2,
                targetPrice = 1900.0,
                direction = "BELOW",
                label = "Drop alert",
                isActive = true,
                createdAt = 0,
                lastTriggeredAt = null
            )
        )

        // Set up current price: $1890 (fell below $1900)
        repository.latestPriceFlow.value = GoldPriceEntity(
            id = 1,
            pricePerOunce = 1890.0,
            currency = "USD",
            timestamp = 1000,
            source = "test"
        )

        // Execute use case
        useCase()

        // Assertions
        assertNotNull(triggeredTitle)
        assertEquals("Gold Alert!", triggeredTitle)
        assertTrue(triggeredBody!!.contains("fell below $1900.0"))
        assertTrue(triggeredBody!!.contains("Current: $1890.0"))
        
        // Assert database is updated (deactivated)
        assertEquals(1, repository.updatedThresholds.size)
        assertFalse(repository.updatedThresholds[0].isActive)
    }
}
