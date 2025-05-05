// CurrencyApiService.kt
package `in`.amankumar110.currencyconverterapp.data.remote

import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyApiResponse
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("de1458fb1d0beb4fb6e7bf1f/latest/USD")
    suspend fun getCurrencyRates(): CurrencyApiResponse
}
