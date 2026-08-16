package com.example.goldalert.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A user-defined price alert threshold.
 *
 * When a new gold price is written to [GoldPriceEntity], the domain layer
 * evaluates all active thresholds and fires a local notification if crossed.
 */
@Entity(tableName = "thresholds")
data class ThresholdEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Target price in USD per troy ounce. */
    val targetPrice: Double,

    /**
     * Direction of the alert.
     * "ABOVE" → alert when live price rises above [targetPrice].
     * "BELOW" → alert when live price falls below [targetPrice].
     */
    val direction: String,

    /** Optional human-readable label (e.g. "Sell signal", "Buy dip"). */
    val label: String = "",

    /** When false, the threshold is paused and will not trigger alerts. */
    val isActive: Boolean = true,

    /** Epoch millis when this threshold was created by the user. */
    val createdAt: Long,

    /**
     * Epoch millis of the last time this threshold triggered a notification.
     * Used to prevent duplicate alerts for the same crossing event.
     */
    val lastTriggeredAt: Long? = null
)
