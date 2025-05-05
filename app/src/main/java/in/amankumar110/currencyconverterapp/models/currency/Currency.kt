package `in`.amankumar110.currencyconverterapp.models.currency

data class Currency(val code : String, val name : String) {

    fun getCommonName(): String {
        return "$name - $code"
    }
}