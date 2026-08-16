package com.bullish.goldalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goldalert.presentation.DashboardViewModel
import com.example.goldalert.presentation.ThresholdListViewModel
import com.example.goldalert.presentation.CandleChartViewModel
import com.bullish.goldalert.ui.chart.CandleChartScreen
import com.bullish.goldalert.ui.dashboard.DashboardScreen
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext

val DarkBg = Color(0xFF0F0F11)
val SlateCard = Color(0xFF1B1B1F)
val GoldLight = Color(0xFFFFD700)
val GoldDark = Color(0xFFC5A01A)
val GreenActive = Color(0xFF32CD32)
val RedActive = Color(0xFFEF5350)
val TextLight = Color(0xFFF5F5F7)
val TextMuted = Color(0xFF8E8E93)

class MainActivity : ComponentActivity() {
    private val dashboardViewModel: DashboardViewModel by inject()
    private val thresholdViewModel: ThresholdListViewModel by inject()
    private val candleChartViewModel: CandleChartViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KoinContext {
                App(
                    dashboardViewModel = dashboardViewModel,
                    thresholdViewModel = thresholdViewModel,
                    candleChartViewModel = candleChartViewModel
                )
            }
        }
    }
}

@Composable
fun App(
    dashboardViewModel: DashboardViewModel,
    thresholdViewModel: ThresholdListViewModel,
    candleChartViewModel: CandleChartViewModel
) {
    val latestPrice by dashboardViewModel.latestPrice.collectAsState()
    val syncState by dashboardViewModel.syncState.collectAsState()
    val thresholds by thresholdViewModel.thresholds.collectAsState()

    val navController = rememberNavController()

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = DarkBg,
            surface = SlateCard,
            primary = GoldLight,
            onPrimary = Color.Black
        )
    ) {
        NavHost(
            navController = navController,
            startDestination = "dashboard"
        ) {
            composable("dashboard") {
                DashboardScreen(
                    latestPrice = latestPrice,
                    syncState = syncState,
                    thresholds = thresholds,
                    onRefresh = { dashboardViewModel.refreshPrice() },
                    onOpenChart = { navController.navigate("chart") },
                    onAddThreshold = { price, direction, label ->
                        thresholdViewModel.addThreshold(price, direction, label)
                    },
                    onToggleThreshold = { thresholdViewModel.toggleThreshold(it) },
                    onDeleteThreshold = { thresholdViewModel.deleteThreshold(it) }
                )
            }
            composable("chart") {
                CandleChartScreen(
                    onBackClick = { navController.popBackStack() },
                    viewModel = candleChartViewModel
                )
            }
        }
    }
}