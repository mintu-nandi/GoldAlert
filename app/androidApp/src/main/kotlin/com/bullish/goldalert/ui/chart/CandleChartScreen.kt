package com.bullish.goldalert.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bullish.goldalert.DarkBg
import com.bullish.goldalert.GoldLight
import com.bullish.goldalert.GreenActive
import com.bullish.goldalert.RedActive
import com.bullish.goldalert.SlateCard
import com.bullish.goldalert.TextLight
import com.bullish.goldalert.TextMuted
import com.example.goldalert.presentation.CandleChartViewModel
import com.example.goldalert.presentation.model.CandleEntry
import com.example.goldalert.presentation.model.ChartTimeframe
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CandleChartScreen(
    onBackClick: () -> Unit,
    viewModel: CandleChartViewModel
) {
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val candles by viewModel.candles.collectAsState()

    var hoveredCandleIndex by remember { mutableStateOf<Int?>(null) }
    val displayCandle = hoveredCandleIndex?.let { idx ->
        candles.getOrNull(idx)
    } ?: candles.lastOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = 16.dp)
    ) {
        // 1. Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextLight
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "GOLD CANDLESTICK CHART",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextLight,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "XAU / USD • Real-time OHLC Feed",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Selected Candle Stats Header
        CandleStatsHeader(candle = displayCandle)

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Custom Candlestick Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SlateCard.copy(alpha = 0.6f))
                .border(1.dp, SlateCard, RoundedCornerShape(16.dp))
        ) {
            if (candles.isNotEmpty()) {
                CandleChartCanvas(
                    candles = candles,
                    timeframe = selectedTimeframe,
                    onHoverIndexChanged = { hoveredCandleIndex = it }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Timeframe Selector Bar
        TimeframeSelectorBar(
            selectedTimeframe = selectedTimeframe,
            onTimeframeSelected = { tf ->
                hoveredCandleIndex = null
                viewModel.setTimeframe(tf)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CandleStatsHeader(candle: CandleEntry?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (candle != null) {
                    val isUp = candle.isBullish
                    val color = if (isUp) GreenActive else RedActive
                    val sign = if (candle.changeAmount >= 0) "+" else ""

                    Text(
                        text = String.format("$%,.2f", candle.close),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )

                    Surface(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = String.format("%s%.2f (%.2f%%)", sign, candle.changeAmount, candle.changePercentage),
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Text(text = "Loading data...", color = TextMuted, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (candle != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(label = "O", value = String.format("%.2f", candle.open))
                    StatItem(label = "H", value = String.format("%.2f", candle.high))
                    StatItem(label = "L", value = String.format("%.2f", candle.low))
                    StatItem(label = "C", value = String.format("%.2f", candle.close))
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
    }
}

@Composable
fun CandleChartCanvas(
    candles: List<CandleEntry>,
    timeframe: ChartTimeframe,
    onHoverIndexChanged: (Int?) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    var touchX by remember { mutableStateOf<Float?>(null) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(candles) {
                detectTapGestures(
                    onPress = { offset ->
                        touchX = offset.x
                        val candleWidth = size.width / candles.size
                        val index = (offset.x / candleWidth).toInt().coerceIn(0, candles.lastIndex)
                        onHoverIndexChanged(index)
                    }
                )
            }
            .pointerInput(candles) {
                detectTransformGestures { _, panOffset, _, _ ->
                    val currentX = touchX ?: (size.width / 2f)
                    val newX = currentX + panOffset.x
                    touchX = newX
                    val candleWidth = size.width / candles.size
                    val index = (newX / candleWidth).toInt().coerceIn(0, candles.lastIndex)
                    onHoverIndexChanged(index)
                }
            }
    ) {
        val width = size.width
        val height = size.height

        val paddingBottom = 30.sp.toPx()
        val paddingRight = 60.sp.toPx()
        val chartWidth = width - paddingRight
        val chartHeight = height - paddingBottom

        val minPrice = candles.minOf { it.low }
        val maxPrice = candles.maxOf { it.high }
        val priceRange = if (maxPrice - minPrice == 0.0) 1.0 else maxPrice - minPrice

        // Helper to project price to canvas Y coordinate
        fun priceToY(price: Double): Float {
            return chartHeight - (((price - minPrice) / priceRange) * chartHeight).toFloat()
        }

        // Draw horizontal grid lines & price labels
        val gridCount = 4
        for (i in 0..gridCount) {
            val y = chartHeight * (i.toFloat() / gridCount)
            val priceVal = maxPrice - ((priceRange / gridCount) * i)

            drawLine(
                color = Color.DarkGray.copy(alpha = 0.4f),
                start = Offset(0f, y),
                end = Offset(chartWidth, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )

            drawText(
                textMeasurer = textMeasurer,
                text = String.format("%.1f", priceVal),
                topLeft = Offset(chartWidth + 6.dp.toPx(), y - 10.dp.toPx()),
                style = TextStyle(color = TextMuted, fontSize = 10.sp)
            )
        }

        val candleCount = candles.size
        val candleSlotWidth = chartWidth / candleCount
        val candleBodyWidth = (candleSlotWidth * 0.7f).coerceAtLeast(3f)

        // Draw candles
        candles.forEachIndexed { index, candle ->
            val x = (index * candleSlotWidth) + (candleSlotWidth / 2f)
            val highY = priceToY(candle.high)
            val lowY = priceToY(candle.low)
            val openY = priceToY(candle.open)
            val closeY = priceToY(candle.close)

            val color = if (candle.isBullish) GreenActive else RedActive

            // Draw Wick
            drawLine(
                color = color,
                start = Offset(x, highY),
                end = Offset(x, lowY),
                strokeWidth = 2f
            )

            // Draw Candle Body
            val bodyTop = minOf(openY, closeY)
            val bodyHeight = maxOf(Math.abs(openY - closeY), 2f)

            drawRect(
                color = color,
                topLeft = Offset(x - (candleBodyWidth / 2f), bodyTop),
                size = Size(candleBodyWidth, bodyHeight)
            )
        }

        // Draw permanent x-axis timeline labels at bottom
        if (candles.size >= 2) {
            val startText = formatTimestamp(candles.first().timestamp, timeframe)
            val endText = formatTimestamp(candles.last().timestamp, timeframe)
            val midText = formatTimestamp(candles[candles.size / 2].timestamp, timeframe)

            // Start Label
            drawText(
                textMeasurer = textMeasurer,
                text = startText,
                topLeft = Offset(10.dp.toPx(), chartHeight + 4.dp.toPx()),
                style = TextStyle(color = TextMuted, fontSize = 9.sp)
            )

            // Middle Label
            drawText(
                textMeasurer = textMeasurer,
                text = midText,
                topLeft = Offset(chartWidth / 2f - 30.dp.toPx(), chartHeight + 4.dp.toPx()),
                style = TextStyle(color = TextMuted, fontSize = 9.sp)
            )

            // End Label
            drawText(
                textMeasurer = textMeasurer,
                text = endText,
                topLeft = Offset(chartWidth - 75.dp.toPx(), chartHeight + 4.dp.toPx()),
                style = TextStyle(color = TextMuted, fontSize = 9.sp)
            )
        }

        // Draw Touch Crosshair
        touchX?.let { tx ->
            val clampedX = tx.coerceIn(0f, chartWidth)
            val index = (clampedX / candleSlotWidth).toInt().coerceIn(0, candles.lastIndex)
            val candle = candles[index]
            val candleX = (index * candleSlotWidth) + (candleSlotWidth / 2f)
            val closeY = priceToY(candle.close)

            // Vertical crosshair line
            drawLine(
                color = GoldLight,
                start = Offset(candleX, 0f),
                end = Offset(candleX, chartHeight),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Horizontal crosshair line
            drawLine(
                color = GoldLight,
                start = Offset(0f, closeY),
                end = Offset(chartWidth, closeY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Draw timestamp label at bottom
            val timeText = formatTimestamp(candle.timestamp, timeframe)
            drawText(
                textMeasurer = textMeasurer,
                text = timeText,
                topLeft = Offset((candleX - 40.dp.toPx()).coerceIn(0f, chartWidth - 80.dp.toPx()), chartHeight + 4.dp.toPx()),
                style = TextStyle(color = GoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun TimeframeSelectorBar(
    selectedTimeframe: ChartTimeframe,
    onTimeframeSelected: (ChartTimeframe) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ChartTimeframe.values()) { timeframe ->
            val isSelected = timeframe == selectedTimeframe
            val containerColor = if (isSelected) GoldLight else SlateCard
            val textColor = if (isSelected) Color.Black else TextLight

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(containerColor)
                    .clickable { onTimeframeSelected(timeframe) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeframe.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long, timeframe: ChartTimeframe): String {
    return try {
        val pattern = when (timeframe) {
            ChartTimeframe.M1, ChartTimeframe.M5, ChartTimeframe.M15, ChartTimeframe.M30 -> "HH:mm"
            ChartTimeframe.H1, ChartTimeframe.H4 -> "MMM dd, HH:mm"
            ChartTimeframe.D1, ChartTimeframe.W1, ChartTimeframe.YTD -> "MMM dd"
            ChartTimeframe.MO1, ChartTimeframe.MO3, ChartTimeframe.MO6 -> "MMM yyyy"
            ChartTimeframe.Y1, ChartTimeframe.Y5, ChartTimeframe.ALL -> "yyyy"
        }
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
