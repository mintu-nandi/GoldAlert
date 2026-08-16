package com.example.goldalert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted gold price snapshot from the API.
 *
 * SSOT rule: The repository writes here after every successful network fetch.
 * The UI NEVER reads directly from the network — it observes this table via Flow.
 */
@Entity(tableName = "gold_prices")
data class GoldPriceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Spot price in USD per troy ounce (31.1035 g). */
    val pricePerOunce: Double,

    /** Currency code, default USD. */
    val currency: String = "USD",

    /** Epoch millis at which this price was fetched from the API. */
    val timestamp: Long,

    /** Identifier of the data source / API endpoint. */
    val source: String = "API"
)
