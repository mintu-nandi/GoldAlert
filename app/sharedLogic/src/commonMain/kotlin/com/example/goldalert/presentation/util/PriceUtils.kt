package com.example.goldalert.presentation.util

import com.example.goldalert.data.local.entity.GoldPriceEntity
import kotlin.math.abs

fun List<GoldPriceEntity>.calculateDailyChange(): Pair<Double, Double> {
    if (this.size < 2) return Pair(0.0, 0.0)
    
    val latest = this.first()
    val targetTimestamp = latest.timestamp - (24 * 60 * 60 * 1000L)
    
    // Find the closest point to 24 hours ago, or fallback to the oldest available
    val previousClose = this.minByOrNull { abs(it.timestamp - targetTimestamp) } ?: this.last()
    
    val changeAmount = latest.pricePerOunce - previousClose.pricePerOunce
    val changePercent = if (previousClose.pricePerOunce != 0.0) {
        (changeAmount / previousClose.pricePerOunce) * 100.0
    } else {
        0.0
    }
    
    return Pair(changeAmount, changePercent)
}
