package `in`.amankumar110.currencyconverterapp.domain.contract

import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import `in`.amankumar110.currencyconverterapp.models.currency.ExchangeRate

interface CurrencyRepository {

    suspend fun getExchangeRates(): CurrencyListener<List<ExchangeRate>>

    sealed class CurrencyListener<out T> {
        data class Success<out T>(val data: T) : CurrencyListener<T>()
        data class Error(val error: Exception) : CurrencyListener<Nothing>()
    }

}
