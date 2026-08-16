package com.bullish.goldalert.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.goldalert.domain.usecase.SyncGoldPriceUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class PriceSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val syncGoldPriceUseCase: SyncGoldPriceUseCase by inject()

    override suspend fun doWork(): Result {
        Log.d("PriceSyncWorker", "🟢 Android: Background PriceSyncWorker triggered")
        val result = syncGoldPriceUseCase()
        
        // Reschedule itself to execute again in 5 minutes
        reschedule(applicationContext)
        
        return if (result.isSuccess) {
            Log.d("PriceSyncWorker", "🟢 Android: Background PriceSyncWorker sync succeeded")
            Result.success()
        } else {
            Log.e("PriceSyncWorker", "🔴 Android: Background PriceSyncWorker sync failed: ${result.exceptionOrNull()?.message}")
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "PriceSyncWorker"

        fun enqueueInitial(context: Context) {
            Log.d(TAG, "🟢 Android: Enqueuing initial 5-minute background refresh task")
            val workRequest = OneTimeWorkRequest.Builder(PriceSyncWorker::class.java)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        private fun reschedule(context: Context) {
            Log.d(TAG, "🟢 Android: Rescheduling next 5-minute background refresh task")
            val workRequest = OneTimeWorkRequest.Builder(PriceSyncWorker::class.java)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
