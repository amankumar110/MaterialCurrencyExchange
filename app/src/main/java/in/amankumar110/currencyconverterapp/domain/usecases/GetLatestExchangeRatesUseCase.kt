package `in`.amankumar110.currencyconverterapp.domain.usecases

import android.util.Log
import `in`.amankumar110.currencyconverterapp.domain.contract.CurrencyRepository
import `in`.amankumar110.currencyconverterapp.models.currency.ExchangeRate
import javax.inject.Inject

class GetLatestExchangeRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {

    suspend fun execute(): Result<List<ExchangeRate>> {
        return try {
            // Call the repository to fetch exchange rates
            val response = repository.getExchangeRates()

            // Handle the response based on its type
            when (response) {
                is CurrencyRepository.CurrencyListener.Success -> {
                    // Successfully fetched data, return the list of currencies
                    Result.success(response.data)
                }
                is CurrencyRepository.CurrencyListener.Error -> {
                    // Log the error and return null (or handle differently, e.g., show a fallback)
                    Log.e("GetLatestExchangeRates", "Error fetching exchange rates: ${response.error}")
                    Result.failure(response.error)
                }
            }
        } catch (e: Exception) {
            // Catch any unforeseen exceptions and log them
            Log.e("GetLatestExchangeRates", "Unexpected error: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}
