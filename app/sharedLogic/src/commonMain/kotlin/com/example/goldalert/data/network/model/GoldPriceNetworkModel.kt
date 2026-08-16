package com.example.goldalert.data.network.model

import kotlinx.serialization.Serializable

/**
 * Data transfer object matching response from https://api.gold-api.com/price/XAU/USD
 *
 * Example:
 * ```json
 * {
 *   "currency": "USD",
 *   "currencySymbol": "$",
 *   "exchangeRate": 1.0,
 *   "name": "Gold",
 *   "price": 4377.60,
 *   "symbol": "XAU",
 *   "updatedAt": "2026-08-16T09:23:58Z",
 *   "updatedAtReadable": "a few seconds ago"
 * }
 * ```
 */
@Serializable
data class GoldPriceNetworkModel(
    val currency: String = "USD",
    val price: Double,
    val symbol: String = "XAU",
    val name: String = "Gold",
    val updatedAt: String? = null
)
