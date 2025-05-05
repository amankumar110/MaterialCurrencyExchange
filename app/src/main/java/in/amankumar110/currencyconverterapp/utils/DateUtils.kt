package `in`.amankumar110.currencyconverterapp.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object DateUtils {
        val currentDateTime: String
            get() {
                val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
                return sdf.format(Date())
            }

        val currentDateOnly: String
            get() {
                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                return sdf.format(Date())
            }

        fun getDateFrom(dateString: String): Date {

            val formatter = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
            return formatter.parse(dateString)
        }

        fun getDateStringFrom(date: Date): String {
            val formatter = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
            return formatter.format(date)
        }

    }
}