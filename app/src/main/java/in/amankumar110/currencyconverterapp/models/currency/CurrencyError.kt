package `in`.amankumar110.currencyconverterapp.models.currency

sealed class CurrencyError(override val message: String) : Exception(message) {
    // Represents errors that occur due to network issues
    data class NetworkError(val error: String) : CurrencyError(error)

    // Represents API errors like success flag not being true
    data class ApiError(val error: String) : CurrencyError(error)

    // Represents validation errors, e.g., invalid rates
    data class ValidationError(val error: String) : CurrencyError(error)

    // Represents unknown or general errors
    data class UnknownError(val error: String) : CurrencyError(error)

    // Represents Room DB errors
    data class LocalDatabaseError(val error: String) : CurrencyError(error)

}