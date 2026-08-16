package com.example.goldalert.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldalert.data.local.entity.GoldPriceEntity
import com.example.goldalert.domain.repository.GoldRepository
import com.example.goldalert.presentation.model.CandleEntry
import com.example.goldalert.presentation.model.ChartTimeframe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.math.round
import kotlin.math.abs
import com.example.goldalert.util.watch

class CandleChartViewModel(
    repository: GoldRepository
) : ViewModel() {

    private val _selectedTimeframe = MutableStateFlow(ChartTimeframe.M15)
    val selectedTimeframe: StateFlow<ChartTimeframe> = _selectedTimeframe.asStateFlow()

    val candles: StateFlow<List<CandleEntry>> = combine(
        repository.observePriceHistory(),
        _selectedTimeframe
    ) { history, timeframe ->
        generateCandles(history, timeframe)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setTimeframe(tf: ChartTimeframe) {
        _selectedTimeframe.value = tf
    }

    private fun generateCandles(
        history: List<GoldPriceEntity>,
        timeframe: ChartTimeframe
    ): List<CandleEntry> {
        val sortedHistory = history.sortedBy { it.timestamp }
        if (sortedHistory.isEmpty()) return emptyList()

        val now = Clock.System.now().toEpochMilliseconds()

        val cutoff = when {
            timeframe == ChartTimeframe.YTD -> {
                val currentYear = Clock.System.now().toLocalDateTime(TimeZone.UTC).year
                LocalDate(currentYear, 1, 1).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
            }
            timeframe.lookbackMillis == Long.MAX_VALUE -> Long.MIN_VALUE
            else -> now - timeframe.lookbackMillis
        }

        val filteredTicks = sortedHistory.filter { it.timestamp >= cutoff }

        if (filteredTicks.size >= 2) {
            return groupTicksIntoCandles(filteredTicks, timeframe)
        }

        if (sortedHistory.size >= 2) {
            return groupTicksIntoCandles(sortedHistory, timeframe)
        }

        val basePrice = sortedHistory.lastOrNull()?.pricePerOunce ?: 4377.60
        return generateSyntheticHistoricalCandles(basePrice, timeframe)
    }

    private fun groupTicksIntoCandles(
        sortedTicks: List<GoldPriceEntity>,
        timeframe: ChartTimeframe
    ): List<CandleEntry> {
        val interval = timeframe.intervalMillis
        val grouped = sortedTicks.groupBy { it.timestamp / interval }
            .entries.sortedBy { it.key }

        return grouped.map { entry ->
            val ticksInBucket = entry.value
            val open = ticksInBucket.first().pricePerOunce
            val close = ticksInBucket.last().pricePerOunce
            val high = ticksInBucket.maxOf { it.pricePerOunce }
            val low = ticksInBucket.minOf { it.pricePerOunce }
            val timestamp = entry.key * interval

            CandleEntry(timestamp, open, high, low, close)
        }
    }

    private fun generateSyntheticHistoricalCandles(
        currentPrice: Double,
        timeframe: ChartTimeframe
    ): List<CandleEntry> {
        val candleCount = 40
        val candlesList = mutableListOf<CandleEntry>()
        val now = Clock.System.now().toEpochMilliseconds()
        val interval = timeframe.intervalMillis

        val random = Random(timeframe.name.hashCode())
        var runningPrice = currentPrice

        for (i in (candleCount - 1) downTo 0) {
            val timestamp = now - (i * interval)

            val volatility = when (timeframe) {
                ChartTimeframe.M1, ChartTimeframe.M5, ChartTimeframe.M15, ChartTimeframe.M30 -> 1.5
                ChartTimeframe.H1, ChartTimeframe.H4 -> 4.0
                ChartTimeframe.D1, ChartTimeframe.W1 -> 12.0
                ChartTimeframe.MO1, ChartTimeframe.MO3, ChartTimeframe.MO6, ChartTimeframe.YTD -> 45.0
                ChartTimeframe.Y1, ChartTimeframe.Y5, ChartTimeframe.ALL -> 120.0
            }

            val change = (random.nextDouble() - 0.49) * volatility
            val open = runningPrice
            val close = round((open + change) * 100.0) / 100.0
            val maxSpike = abs(random.nextDouble() * volatility * 0.6)
            val minSpike = abs(random.nextDouble() * volatility * 0.6)
            val high = round((maxOf(open, close) + maxSpike) * 100.0) / 100.0
            val low = round((minOf(open, close) - minSpike) * 100.0) / 100.0

            candlesList.add(CandleEntry(timestamp, open, high, low, close))
            runningPrice = close
        }

        return candlesList
    }

    fun watchCandles(block: (List<CandleEntry>) -> Unit): () -> Unit = candles.watch(block)
    fun watchSelectedTimeframe(block: (ChartTimeframe) -> Unit): () -> Unit = selectedTimeframe.watch(block)
}
