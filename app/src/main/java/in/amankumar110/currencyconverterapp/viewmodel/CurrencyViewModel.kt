package `in`.amankumar110.currencyconverterapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.amankumar110.currencyconverterapp.domain.usecases.GetLocalExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.models.currency.CurrencyRates
import jakarta.inject.Inject
import androidx.lifecycle.viewModelScope
import `in`.amankumar110.currencyconverterapp.domain.usecases.GetLastUpdatedUseCase
import `in`.amankumar110.currencyconverterapp.domain.usecases.SyncExchangeRatesUseCase
import `in`.amankumar110.currencyconverterapp.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.Date

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val getLocalExchangeRatesUseCase: GetLocalExchangeRatesUseCase,
    private val syncExchangeRatesUseCase: SyncExchangeRatesUseCase,
    private val getLastUpdatedUseCase: GetLastUpdatedUseCase
) : ViewModel() {

    private val _currencyRates = MutableLiveData<CurrencyRates?>(null)
    val currencyRates: LiveData<CurrencyRates?> = _currencyRates

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _lastUpdated = MutableLiveData<Date>(null)
    val lastUpdated: LiveData<Date> = _lastUpdated

    private val _exchangeRatesReady = MutableLiveData<Boolean>(false)
    val exchangeRatesReady: LiveData<Boolean> = _exchangeRatesReady

    // Function to update lastUpdated time automatically

    // Function to check if both exchange rates and last updated time are available
    private fun checkIfExchangeRatesAreReady() {
        if (_currencyRates.value != null && _lastUpdated.value != null) {
            _exchangeRatesReady.value = true
        }
    }

    fun getLocalExchangeRates() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            val exchangeRateResult = getLocalExchangeRatesUseCase.execute()

            _isLoading.postValue(false)

            if (exchangeRateResult.isSuccess) {
                _currencyRates.postValue(exchangeRateResult.getOrDefault(null))
                _lastUpdated.value = getLastUpdatedUseCase.execute()
                checkIfExchangeRatesAreReady()  // Check if both exchange rates and last updated are ready
            } else {
                _errorMessage.value = exchangeRateResult.exceptionOrNull()?.message
            }
        }
    }

    fun refreshExchangeRates() {
        _isLoading.value = true

        viewModelScope.launch {
            val isSuccess = syncExchangeRatesUseCase.execute()
            if (isSuccess) {
                val updatedRatesResult = getLocalExchangeRatesUseCase.execute() // Re-fetch from DB
                _isLoading.value = false

                if (updatedRatesResult.isFailure) {
                    _errorMessage.value = updatedRatesResult.exceptionOrNull()?.message
                    return@launch
                } else {
                    _currencyRates.value = updatedRatesResult.getOrDefault(null)
                    _lastUpdated.value = getLastUpdatedUseCase.execute()
                    checkIfExchangeRatesAreReady()  // Check if both exchange rates and last updated are ready
                }
            }
        }
    }

    fun convertAmount(currencyCodeFrom: String, currencyCodeTo: String, amount: Double): Double? {
        val usdToValueFrom = currencyRates.value?.rates?.find { it.code.lowercase() == currencyCodeFrom.lowercase() }?.rate
        val usdToValueTo = currencyRates.value?.rates?.find { it.code.lowercase() == currencyCodeTo.lowercase() }?.rate

        Log.v("Rate", usdToValueFrom.toString())
        Log.v("Rate", usdToValueTo.toString())

        if (usdToValueFrom == null || usdToValueTo == null) {
            _errorMessage.value = "Conversion failed"
        } else {
            return amount * (usdToValueTo / usdToValueFrom)
        }
        return null
    }
}
