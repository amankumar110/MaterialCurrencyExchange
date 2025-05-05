package `in`.amankumar110.currencyconverterapp.utils

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import `in`.amankumar110.currencyconverterapp.models.currency.Currency
import java.io.ByteArrayOutputStream
import java.io.IOException

class JsonUtils {
    companion object JsonUtils {

        fun readRawJson(context: Context, @RawRes resId: Int): String? {
            val inputStream = context.resources.openRawResource(resId)
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var size: Int
            try {
                while ((inputStream.read(buffer).also { size = it }) != -1) {
                    outputStream.write(buffer, 0, size)
                }
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return outputStream.toString()
        }

        val parseCurrencies = { json: String ->
            val type = object : TypeToken<Map<String, String>>() {}.type
            val currencyMap: Map<String, String> = Gson().fromJson(json, type)

            // Convert each entry of the map into a Currency object and return as a list
            currencyMap.map { entry ->
                Currency(code = entry.key, name = entry.value)
            }
        }
    }
}
