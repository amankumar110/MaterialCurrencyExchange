package `in`.amankumar110.currencyconverterapp.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.amankumar110.currencyconverterapp.data.database.CurrencyDatabase
import `in`.amankumar110.currencyconverterapp.data.local.LocalCurrencyRatesService
import `in`.amankumar110.currencyconverterapp.data.remote.CurrencyApiService
import `in`.amankumar110.currencyconverterapp.data.repository.CurrencyRepositoryImpl
import `in`.amankumar110.currencyconverterapp.data.repository.LocalCurrencyRepositoryImpl
import `in`.amankumar110.currencyconverterapp.domain.contract.CurrencyRepository
import `in`.amankumar110.currencyconverterapp.domain.contract.LocalCurrencyRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val BASE_URL = "https://v6.exchangerate-api.com/v6/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCurrencyApiService(retrofit: Retrofit): CurrencyApiService =
        retrofit.create(CurrencyApiService::class.java)

    @Provides
    @Singleton
    fun provideCurrencyRepository(currencyApiService: CurrencyApiService): CurrencyRepository =
        CurrencyRepositoryImpl(currencyApiService)

    @Provides
    @Singleton
    fun provideLocalCurrencyRatesService(
        @ApplicationContext applicationContext: Context
    ): LocalCurrencyRatesService {

        return Room.databaseBuilder(applicationContext,
            CurrencyDatabase::class.java,
            "currency_rates_db")
            .fallbackToDestructiveMigration(true)
            .build()
            .currencyRatesService()
    }

    @Provides
    @Singleton
    fun provideLocalCurrencyRepository(
        localCurrencyRatesService: LocalCurrencyRatesService
    ): LocalCurrencyRepository {

        return LocalCurrencyRepositoryImpl(localCurrencyRatesService)
    }

}