package `in`.amankumar110.currencyconverterapp.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import `in`.amankumar110.currencyconverterapp.domain.contract.CurrencyRepository
import `in`.amankumar110.currencyconverterapp.domain.usecases.GetLatestExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.domain.usecases.SaveExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.domain.usecases.SyncExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates
import `in`.amankumar110.currencyconverterapp.module.SyncExchangeRatesEntryPoint
import java.util.*


class SyncExchangeRateWorker(
     context: Context,
     workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private var syncExchangeRatesUseCase : SyncExchangeRatesUseCase

    init {

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            SyncExchangeRatesEntryPoint::class.java
        )
        syncExchangeRatesUseCase = entryPoint.provideSyncExchangeRatesUseCase()

    }

    override suspend fun doWork(): Result {

        Log.v("SyncWorker","Data Sync Started")

        val isSuccess = syncExchangeRatesUseCase.execute()

        return when (isSuccess) {
            true -> {
                Log.v("SyncWorker","Data Updated")
                Result.success()}
            false -> {
                Log.v("SyncWorker","Data Not Updated")
                Result.failure()}
        }


    }

}
