package com.bullish.goldalert.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.ImageProvider
import com.bullish.goldalert.R
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.goldalert.domain.repository.GoldRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

import kotlinx.coroutines.flow.firstOrNull
import androidx.glance.appwidget.updateAll
import com.example.goldalert.presentation.util.calculateDailyChange

class GoldWidget : GlanceAppWidget(), KoinComponent {

    private val repository: GoldRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val latestPrices by repository.observePriceHistory().collectAsState(initial = emptyList())
                val currentPrice = latestPrices.firstOrNull()
                
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .appWidgetBackground()
                        .background(ImageProvider(R.drawable.widget_bg_gold_border))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GOLD SPOT PRICE",
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color(0xFFEAB308)),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        
                        val priceText = currentPrice?.let { "$${String.format("%.2f", it.pricePerOunce)}" } ?: "Loading..."
                        
                        Text(
                            text = priceText,
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color.White),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                        
                        if (latestPrices.size > 1) {
                            val (changeAmount, changePercent) = latestPrices.calculateDailyChange()
                            val isPositive = changeAmount >= 0
                            val color = if (isPositive) Color(0xFF32CD32) else Color(0xFFEF5350)
                            val sign = if (isPositive) "+" else ""

                            Spacer(modifier = GlanceModifier.size(4.dp))
                            
                            Box(
                                modifier = GlanceModifier
                                    .background(color.copy(alpha = 0.2f))
                                    .cornerRadius(8.dp)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%s%.2f (%.2f%%)", sign, changeAmount, changePercent),
                                    style = TextStyle(
                                        color = androidx.glance.unit.ColorProvider(color),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        
                        Column(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B))
                                .cornerRadius(8.dp)
                                .padding(vertical = 8.dp)
                                .clickable(actionRunCallback<RefreshAction>()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Refresh",
                                style = TextStyle(
                                    color = androidx.glance.unit.ColorProvider(Color(0xFF94A3B8)),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

class RefreshAction : ActionCallback, KoinComponent {
    private val repository: GoldRepository by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Fetch new price
        repository.fetchAndSaveLatestPrice()
        
        // Explicitly trigger a UI update for the widget
        GoldWidget().update(context, glanceId)
    }
}
