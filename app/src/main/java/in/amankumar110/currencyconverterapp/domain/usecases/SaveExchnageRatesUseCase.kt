package `in`.amankumar110.currencyconverterapp.domain.usecases

import android.util.Log
import `in`.amankumar110.currencyconverterapp.domain.contract.LocalCurrencyRepository
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates
import javax.inject.Inject

class SaveExchangeRatesUseCase @Inject constructor(private val localCurrencyRepository: LocalCurrencyRepository){

    suspend fun execute(currencyRates: CurrencyRates) : Result<Boolean> {

        return try {

            val result = localCurrencyRepository.saveCurrencyRates(currencyRates)
            Result.success(result)
        } catch (e: Exception) {
            Log.e("SaveExchangeRatesUseCase", "Error saving exchange rates: ${e.localizedMessage}")
            Result.failure(e)
        }

    }
}