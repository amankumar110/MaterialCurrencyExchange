package `in`.amankumar110.currencyconverterapp.domain.usecases

import android.util.Log
import `in`.amankumar110.currencyconverterapp.domain.contract.LocalCurrencyRepository
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyError
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates
import javax.inject.Inject

class GetLocalExchangeRatesUseCase @Inject constructor(
    private val localCurrencyRepository: LocalCurrencyRepository
) {

    suspend fun execute(): Result<CurrencyRates> {

        return try {
            var result = localCurrencyRepository.getCurrencyRates()
            Result.success(result)
        } catch (e: Exception) {
            Log.e("GetLocalExchangeRatesUseCase", "Error fetching local exchange rates: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}
