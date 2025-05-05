package `in`.amankumar110.currencyconverterapp.domain.usecases

import android.util.Log
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyError
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates
import `in`.amankumar110.currencyconverterapp.utils.DateUtils
import java.util.Date
import javax.inject.Inject

class SyncExchangeRatesUseCase @Inject constructor(
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase,
    private val saveExchangeRatesUseCase: SaveExchangeRatesUseCase,
    private val getLastUpdatedUseCase: GetLastUpdatedUseCase
) {

    companion object {
        private const val FRESHNESS_THRESHOLD_MILLIS = 30 * 60 * 1000L // 1 hour
    }

    suspend fun execute(): Boolean {
        return try {

            val lastUpdated = getLastUpdatedUseCase.execute()

            // Check if the last update was within the last hour
            val isFresh = System.currentTimeMillis() - lastUpdated.time < FRESHNESS_THRESHOLD_MILLIS
            if (isFresh) {
                Log.v("ApiOptimization", "Prevented data reload: last batch is within the last hour")
                return true
            }

            val exchangeRatesResult = getLatestExchangeRatesUseCase.execute()

            if (exchangeRatesResult.isFailure) {
                Log.e("SyncExchangeRates", "Fetching latest exchange rates failed: ${exchangeRatesResult.exceptionOrNull()}")
                return false
            }

            val data = exchangeRatesResult.getOrNull() ?: run {
                Log.e("SyncExchangeRates", "Exchange rates data was null despite success result")
                return false
            }

            val currentTime = DateUtils.DateUtils.currentDateTime
            val saveResult = saveExchangeRatesUseCase.execute(CurrencyRates(currentTime, data))

            if (saveResult.isFailure) {
                Log.e("SyncExchangeRates", "Saving exchange rates failed: ${saveResult.exceptionOrNull()}")
            }

            saveResult.getOrDefault(false)
        } catch (e: Exception) {
            Log.e("SyncExchangeRates", "Unexpected error during sync: ${e.localizedMessage}", e)
            false
        }
    }
}
