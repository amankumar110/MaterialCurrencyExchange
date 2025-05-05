package `in`.amankumar110.currencyconverterapp.models.currency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "currency_rates")
@TypeConverters(CurrencyListConverter::class)
data class CurrencyRates(
    @ColumnInfo(name = "last_updated")
    val lastUpdated: String,

    @ColumnInfo(name = "rates")
    val rates: List<ExchangeRate>,

    @PrimaryKey()
    val primaryKey: Int = 101
)
