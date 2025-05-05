package `in`.amankumar110.currencyconverterapp.utils

import android.content.Context
import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import java.util.Locale



object CalculationHistory {

    fun getDefaultCurrencyFrom(): Currency {
        return Currency("USD", "United States Dollar")
    }

    fun getDefaultCurrencyTo(context: Context): Currency {
        return try {
            val currentLocale: Locale = context.resources.configuration.locales.get(0)
            val currency: java.util.Currency = java.util.Currency.getInstance(currentLocale)
            val currencyCode: String = currency.currencyCode // e.g., "PKR"
            val currencyName: String = currency.getDisplayName(currentLocale)

            Currency(currencyCode, currencyName)
        } catch (e: Exception) {
            // Fallback to Euro if locale currency cannot be determined
            Currency("EUR", "Euro")
        }
    }

    fun getDefaultAmount(): Double {
        return 1.0
    }
}