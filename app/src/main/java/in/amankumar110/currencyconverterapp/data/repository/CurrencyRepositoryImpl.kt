package `in`.amankumar110.currencyconverterapp.data.repository

import `in`.amankumar110.currencyconverterapp.data.remote.CurrencyApiService
import `in`.amankumar110.currencyconverterapp.domain.contract.CurrencyRepository
import `in`.amankumar110.currencyconverterapp.domain.contract.CurrencyRepository.CurrencyListener
import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyError
import `in`.amankumar110.currencyconverterapp.models.currency.ExchangeRate
import java.net.UnknownHostException
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService,
) : CurrencyRepository {

    override suspend fun getExchangeRates(): CurrencyListener<List<ExchangeRate>> {
        return try {
            // API Call to fetch exchange rates
            val response = apiService.getCurrencyRates()

            // Check if the API response is successful
            if (response.result.lowercase() != "success") {
                // Returning an API error with a meaningful message
                throw CurrencyError.ApiError("API returned an unsuccessful result: ${response.result}")
            }

            // If the data is empty or invalid, throw a validation error
            val data = response.conversion_rates
            if (data.isEmpty()) {
                throw CurrencyError.ValidationError("No conversion rates found in the API response.")
            }

            // Map the data to the list of Currency objects
            val currencyList = getCurrencyListFromData(data)
            CurrencyListener.Success(currencyList)

        } catch (e: UnknownHostException) {
            // Handle network error with a more meaningful message
            CurrencyListener.Error(CurrencyError.NetworkError("No internet connection. Please check your connection."))
        } catch (e: CurrencyError) {
            // Catch custom errors and return them as is
            CurrencyListener.Error(e)
        } catch (e: Exception) {
            // General fallback for any unknown error
            CurrencyListener.Error(CurrencyError.UnknownError("An unexpected error occurred: ${e.localizedMessage}"))
        }
    }

    private fun getCurrencyListFromData(data: Map<String, Double>): List<ExchangeRate> {
        val currencyRates = mutableListOf<ExchangeRate>()
        data.forEach { (currencyCode, rate) ->
            // Validate the rate
            if (rate <= 0) {
                throw CurrencyError.ValidationError("Invalid exchange rate for currency: $currencyCode ($rate)")
            }
            currencyRates.add(ExchangeRate(currencyCode, rate))
        }
        return currencyRates
    }
}
