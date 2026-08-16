package com.bullish.goldalert

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bullish.goldalert.background.PriceSyncWorker
import com.example.goldalert.di.initKoin
import org.koin.android.ext.koin.androidContext
import kotlin.random.Random

class GoldAlertApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            triggerNotification = { title, body ->
                val channelId = "price_alerts"
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Price Alerts",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    manager.createNotificationChannel(channel)
                }
                
                val notification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()
                    
                manager.notify(Random.nextInt(), notification)
            }
        ) {
            androidContext(this@GoldAlertApplication)
        }

        // Start background worker checks
        PriceSyncWorker.enqueueInitial(this)
    }
}
