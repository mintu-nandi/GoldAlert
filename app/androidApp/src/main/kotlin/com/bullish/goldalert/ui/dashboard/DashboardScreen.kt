package com.bullish.goldalert.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bullish.goldalert.R
import com.example.goldalert.data.local.entity.GoldPriceEntity
import com.example.goldalert.data.local.entity.ThresholdEntity
import com.example.goldalert.presentation.SyncState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.goldalert.presentation.util.calculateDailyChange

val DarkBg = Color(0xFF0F0F11)
val SlateCard = Color(0xFF1B1B1F)
val GoldLight = Color(0xFFFFD700)
val GoldDark = Color(0xFFC5A01A)
val GreenActive = Color(0xFF32CD32)
val RedActive = Color(0xFFEF5350)
val TextLight = Color(0xFFF5F5F7)
val TextMuted = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    latestPrice: GoldPriceEntity?,
    priceHistory: List<GoldPriceEntity>,
    syncState: SyncState,
    thresholds: List<ThresholdEntity>,
    onRefresh: () -> Unit,
    onOpenChart: () -> Unit,
    onAddThreshold: (price: Double, direction: String, label: String) -> Unit,
    onToggleThreshold: (ThresholdEntity) -> Unit,
    onDeleteThreshold: (ThresholdEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoldLight,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Threshold")
            }
        },
        containerColor = DarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            HeaderSection(
                syncState = syncState,
                onRefresh = onRefresh,
                onOpenChart = onOpenChart
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    PriceFeedCard(latestPrice = latestPrice, priceHistory = priceHistory)
                }

                item {
                    Text(
                        text = "ACTIVE ALERTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (thresholds.isEmpty()) {
                    item {
                        EmptyThresholdsPlaceholder()
                    }
                } else {
                    items(thresholds, key = { it.id }) { threshold ->
                        ThresholdItemRow(
                            threshold = threshold,
                            onToggle = { onToggleThreshold(threshold) },
                            onDelete = { onDeleteThreshold(threshold) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        if (showAddDialog) {
            AddThresholdDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { price, direction, label ->
                    onAddThreshold(price, direction, label)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun HeaderSection(
    syncState: SyncState,
    onRefresh: () -> Unit,
    onOpenChart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "GOLD SPOT ALERT",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextLight,
                letterSpacing = 0.5.sp
            )
            
            val subtitleText = when (syncState) {
                is SyncState.Loading -> "Syncing Spot Price..."
                is SyncState.Success -> "Live Feed Connected"
                is SyncState.Error -> "Sync failed. Retry"
                else -> "Initialized"
            }
            val subtitleColor = when (syncState) {
                is SyncState.Loading -> GoldLight
                is SyncState.Success -> GreenActive
                is SyncState.Error -> RedActive
                else -> TextMuted
            }

            Text(
                text = subtitleText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = subtitleColor
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .clickable { onOpenChart() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bar_chart),
                    contentDescription = "Historical Chart",
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .clickable(enabled = syncState !is SyncState.Loading) { onRefresh() },
                contentAlignment = Alignment.Center
            ) {
                if (syncState is SyncState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = GoldLight,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync Price",
                        tint = GoldLight
                    )
                }
            }
        }
    }
}

@Composable
fun PriceFeedCard(latestPrice: GoldPriceEntity?, priceHistory: List<GoldPriceEntity>) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SlateCard, Color(0xFF141416))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LIVE PRICE PER OUNCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (latestPrice != null) {
                Text(
                    text = String.format("$%,.2f", latestPrice.pricePerOunce),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                val (changeAmount, changePercent) = priceHistory.calculateDailyChange()
                if (priceHistory.size > 1) {
                    val isPositive = changeAmount >= 0
                    val color = if (isPositive) GreenActive else RedActive
                    val sign = if (isPositive) "+" else ""

                    Surface(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = String.format("%s%.2f (%.2f%%)", sign, changeAmount, changePercent),
                            color = color,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GreenActive.copy(alpha = glowAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live • ${latestPrice.source} • ${formatTimestamp(latestPrice.timestamp)}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            } else {
                Text(
                    text = "$----.--",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Awaiting initial price feed sync...",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun ThresholdItemRow(
    threshold: ThresholdEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val directionColor by animateColorAsState(
        if (threshold.direction == "ABOVE") GreenActive else RedActive
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateCard)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (threshold.direction == "ABOVE") "CROSSES ABOVE" else "DROPS BELOW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = directionColor,
                    letterSpacing = 0.5.sp
                )
                if (threshold.label.isNotEmpty()) {
                    Text(
                        text = " • ${threshold.label}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format("$%,.2f", threshold.targetPrice),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextLight
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Switch(
                checked = threshold.isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldLight,
                    checkedTrackColor = GoldDark
                )
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Alert",
                tint = RedActive.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDelete() }
            )
        }
    }
}

@Composable
fun EmptyThresholdsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Alert Thresholds Defined",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tap the + button to add a target spot price alert",
            fontSize = 11.sp,
            color = TextMuted.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AddThresholdDialog(
    onDismiss: () -> Unit,
    onConfirm: (price: Double, direction: String, label: String) -> Unit
) {
    var priceText by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("ABOVE") }
    var labelText by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Create Price Alert",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val aboveBg = if (direction == "ABOVE") GreenActive.copy(alpha = 0.2f) else SlateCard
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(aboveBg)
                            .clickable { direction = "ABOVE" }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Above",
                            color = if (direction == "ABOVE") GreenActive else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val belowBg = if (direction == "BELOW") RedActive.copy(alpha = 0.2f) else SlateCard
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(belowBg)
                            .clickable { direction = "BELOW" }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Below",
                            color = if (direction == "BELOW") RedActive else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        priceText = it
                        inputError = false
                    },
                    label = { Text("Alert Target Price ($/oz)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = inputError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldLight,
                        focusedLabelColor = GoldLight,
                        unfocusedBorderColor = Color.DarkGray,
                        unfocusedLabelColor = TextMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = labelText,
                    onValueChange = { labelText = it },
                    label = { Text("Label (optional, e.g. Sell Signal)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldLight,
                        focusedLabelColor = GoldLight,
                        unfocusedBorderColor = Color.DarkGray,
                        unfocusedLabelColor = TextMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val priceVal = priceText.toDoubleOrNull()
                            if (priceVal != null && priceVal > 0) {
                                onConfirm(priceVal, direction, labelText)
                            } else {
                                inputError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldLight)
                    ) {
                        Text("CREATE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        "N/A"
    }
}
