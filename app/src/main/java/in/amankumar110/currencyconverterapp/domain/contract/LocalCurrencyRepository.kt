package `in`.amankumar110.currencyconverterapp.domain.contract

import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates

interface LocalCurrencyRepository {

    suspend fun getCurrencyRates(): CurrencyRates
    suspend fun saveCurrencyRates(currencyRates: CurrencyRates) : Boolean
    suspend fun getLastUpdated() : String

}