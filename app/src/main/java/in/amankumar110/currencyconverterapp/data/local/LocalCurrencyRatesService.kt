package `in`.amankumar110.currencyconverterapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates

@Dao
interface LocalCurrencyRatesService {

    // Retrieve the most recent exchange rate
    @Query("SELECT * FROM currency_rates LIMIT 1")
    suspend fun getRates(): CurrencyRates?

    // Insert exchange rates into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(currencyRates: CurrencyRates)

    // Update existing exchange rates
    @Update
    suspend fun updateRates(currencyRates: CurrencyRates)

    @Query("SELECT last_updated FROM currency_rates LIMIT 1")
    suspend fun getLastUpdated(): String?
}
