package `in`.amankumar110.currencyconverterapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import `in`.amankumar110.currencyconverterapp.domain.usecases.SyncExchangeRatesWrapper
import jakarta.inject.Inject

@HiltAndroidApp
class CurrencyConverterApp : Application() {


    @Inject
    lateinit var syncExchangeRatesWrapper: SyncExchangeRatesWrapper

    override fun onCreate() {
        super.onCreate()
        syncExchangeRatesWrapper.execute()
    }
}