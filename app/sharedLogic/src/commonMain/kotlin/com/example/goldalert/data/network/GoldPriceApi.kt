package com.example.goldalert.data.network

import com.example.goldalert.data.network.model.GoldPriceNetworkModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
data class GoldHistoryResponse(
    val symbol: String,
    val interval: String,
    val currency: String,
    val points: List<HistoryPoint>
)

@Serializable
data class HistoryPoint(
    val d: String,
    val c: Double,
    val h: Double,
    val l: Double
)

class GoldPriceApi(
    private val client: HttpClient,
    private val baseUrl: String = DEFAULT_URL
) {
    companion object {
        const val DEFAULT_URL = "https://api.gold-api.com/price/XAU/USD"
        const val HISTORY_URL = "https://xaus.com/api/v1/history"
    }

    suspend fun fetchLatestGoldPrice(): GoldPriceNetworkModel {
        return try {
            client.get(baseUrl).body()
        } catch (e: Exception) {
            client.get(DEFAULT_URL).body()
        }
    }

    suspend fun fetchGoldHistory(): GoldHistoryResponse {
        return client.get(HISTORY_URL).body()
    }
}
