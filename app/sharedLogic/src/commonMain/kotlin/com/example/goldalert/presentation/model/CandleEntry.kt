package com.example.goldalert.presentation.model

data class CandleEntry(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
) {
    val isBullish: Boolean get() = close >= open
    val changeAmount: Double get() = close - open
    val changePercentage: Double get() = if (open != 0.0) ((close - open) / open) * 100.0 else 0.0
}

enum class ChartTimeframe(
    val label: String,
    val intervalMillis: Long,
    val lookbackMillis: Long
) {
    M1(   "1m",  60_000L,                    60 * 60_000L),          // 1-min candles, last 1h
    M5(   "5m",  5 * 60_000L,               4 * 60 * 60_000L),      // 5-min candles, last 4h
    M15(  "15m", 15 * 60_000L,              12 * 60 * 60_000L),     // 15-min candles, last 12h
    M30(  "30m", 30 * 60_000L,              24 * 60 * 60_000L),     // 30-min candles, last 24h
    H1(   "1H",  60 * 60_000L,              3L * 24 * 60 * 60_000L),  // 1H candles, last 3d
    H4(   "4H",  4 * 60 * 60_000L,          10L * 24 * 60 * 60_000L), // 4H candles, last 10d
    D1(   "1D",  24 * 60 * 60_000L,         30L * 24 * 60 * 60_000L), // 1D candles, last 30d
    W1(   "1W",  7L * 24 * 60 * 60_000L,    26L * 7 * 24 * 60 * 60_000L), // 1W candles, last 26w
    MO1(  "1M",  30L * 24 * 60 * 60_000L,   12L * 30 * 24 * 60 * 60_000L), // 1M candles, last 1y
    MO3(  "3M",  90L * 24 * 60 * 60_000L,   3L * 365 * 24 * 60 * 60_000L), // 3M candles, last 3y
    MO6(  "6M",  180L * 24 * 60 * 60_000L,  5L * 365 * 24 * 60 * 60_000L), // 6M candles, last 5y
    YTD(  "YTD", 24 * 60 * 60_000L,         -1L),                   // daily candles since Jan 1, special
    Y1(   "1Y",  24 * 60 * 60_000L,         365L * 24 * 60 * 60_000L),      // daily candles, last 1y
    Y5(   "5Y",  7L * 24 * 60 * 60_000L,    5L * 365 * 24 * 60 * 60_000L), // weekly candles, last 5y
    ALL(  "ALL", 30L * 24 * 60 * 60_000L,   Long.MAX_VALUE)         // monthly candles, all data
}

