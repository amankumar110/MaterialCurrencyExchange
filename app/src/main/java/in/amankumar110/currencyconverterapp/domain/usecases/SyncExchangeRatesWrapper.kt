package `in`.amankumar110.currencyconverterapp.domain.usecases

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.amankumar110.currencyconverterapp.work.SyncExchangeRateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncExchangeRatesWrapper @Inject constructor(@ApplicationContext val appContext: Context)  {

    fun execute() {

        val syncRequest = PeriodicWorkRequestBuilder<SyncExchangeRateWorker>(60, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            "sync_exchange_rates",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest)
    }
}