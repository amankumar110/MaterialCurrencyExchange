package `in`.amankumar110.currencyconverterapp.module

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import `in`.amankumar110.currencyconverterapp.domain.usecases.GetLatestExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.domain.usecases.SaveExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.domain.usecases.SyncExchangeRatesUseCase
import javax.inject.Singleton

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncExchangeRatesEntryPoint {
    fun provideSyncExchangeRatesUseCase(): SyncExchangeRatesUseCase
}
