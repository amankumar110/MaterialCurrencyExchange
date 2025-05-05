package `in`.amankumar110.currencyconverterapp.data.repository

import android.util.Log
import `in`.amankumar110.currencyconverterapp.data.local.LocalCurrencyRatesService
import `in`.amankumar110.currencyconverterapp.domain.contract.LocalCurrencyRepository
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyError
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates
import javax.inject.Inject

class LocalCurrencyRepositoryImpl
@Inject constructor(private val localCurrencyRatesService: LocalCurrencyRatesService)
    : LocalCurrencyRepository {

    override suspend fun getCurrencyRates(): CurrencyRates {
        return try {
            val rates = localCurrencyRatesService.getRates()
            if (rates == null) {
                throw CurrencyError.LocalDatabaseError("No rates found in local database.")
            }
            rates
        } catch (e: Exception) {
            // Log the exception and return a custom error message
            Log.e("LocalCurrencyRepository", "Error fetching currency rates: ${e.localizedMessage}")
            throw CurrencyError.LocalDatabaseError("Error retrieving currency rates: ${e.localizedMessage}")
        }
    }

    override suspend fun saveCurrencyRates(currencyRates: CurrencyRates): Boolean {
        return try {
            // Logging the last updated time
            Log.v("lastUpdates: repo : save", currencyRates.lastUpdated)

            // Insert the currency rates into the local database
            localCurrencyRatesService.insertRates(currencyRates)

            // Return success after insertion
            true
        } catch (e: Exception) {
            // Handle any error and wrap it in a custom error
            Log.e("LocalCurrencyRepository", "Error saving currency rates: ${e.localizedMessage}")
            throw CurrencyError.LocalDatabaseError("Error saving currency rates: ${e.localizedMessage}")
        }
    }

    override suspend fun getLastUpdated(): String {

         try {
            val lastUpdated = localCurrencyRatesService.getLastUpdated()

            if(lastUpdated!=null)
                return lastUpdated
            else
                throw CurrencyError.LocalDatabaseError("No last updated found in local database.")
        } catch (e: Exception) {
            // Log the exception and return a custom error message
            Log.e("LocalCurrencyRepository", "Error fetching last updated: ${e.localizedMessage}")
        }
        return ""
    }
}
