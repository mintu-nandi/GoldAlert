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
                        .cornerRadius(16.dp)
                        .background(Color(0xFF0F172A))
                ) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .background(ImageProvider(R.drawable.widget_bg_gold_border))
                            .padding(20.dp),
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
                        
                        Spacer(modifier = GlanceModifier.size(12.dp))
                        
                        val priceText = currentPrice?.let { "$${String.format("%.2f", it.pricePerOunce)}" } ?: "Loading..."
                        
                        Text(
                            text = priceText,
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color.White),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Spacer(modifier = GlanceModifier.size(16.dp))
                        
                        Column(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E293B))
                                .padding(vertical = 15.dp)
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
        repository.fetchAndSaveLatestPrice()
    }
}
