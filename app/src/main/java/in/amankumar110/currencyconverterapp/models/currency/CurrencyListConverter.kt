package `in`.amankumar110.currencyconverterapp.models.currency

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import `in`.amankumar110.currencyconverterapp.models.currency.Currency

object CurrencyListConverter {

    @TypeConverter
    @JvmStatic
    fun fromCurrencyList(currencyList: List<ExchangeRate>?): String? {
        if (currencyList == null) return null
        return Gson().toJson(currencyList)  // Convert list to JSON string
    }

    @TypeConverter
    @JvmStatic
    fun toCurrencyList(data: String?): List<ExchangeRate>? {
        if (data == null) return null
        val type = object : TypeToken<List<ExchangeRate>>() {}.type
        return Gson().fromJson(data, type)  // Convert JSON string back to list
    }
}
