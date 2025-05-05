package `in`.amankumar110.currencyconverterapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import `in`.amankumar110.currencyconverterapp.data.local.LocalCurrencyRatesService
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyListConverter
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates

@Database(entities = [CurrencyRates::class], version = 2)
@TypeConverters(CurrencyListConverter::class)
abstract class CurrencyDatabase :RoomDatabase(){
    abstract fun currencyRatesService(): LocalCurrencyRatesService
}