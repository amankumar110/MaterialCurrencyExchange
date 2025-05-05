package `in`.amankumar110.currencyconverterapp.domain.usecases

import `in`.amankumar110.currencyconverterapp.domain.contract.LocalCurrencyRepository
import `in`.amankumar110.currencyconverterapp.utils.DateUtils
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

class GetLastUpdatedUseCase @Inject constructor(private val localCurrencyRepository: LocalCurrencyRepository) {

    public suspend fun execute() : Date {
        val lastUpdatedString = localCurrencyRepository.getLastUpdated()

        // Return one hour ago so that system can fetch new data
        if(lastUpdatedString.isEmpty())
            return Date((System.currentTimeMillis()-4_000_000))

        return DateUtils.DateUtils.getDateFrom(lastUpdatedString)
    }
}

