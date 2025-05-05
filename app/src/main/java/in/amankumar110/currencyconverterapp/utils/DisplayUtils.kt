package `in`.amankumar110.currencyconverterapp.utils

import java.math.BigDecimal
import java.math.MathContext

class DisplayUtils {
    companion object {

        fun getFormatedAmount(amount: String): String {
            if (amount.isEmpty() || amount.last() == '.') return amount

            val bigDecimal = try {
                BigDecimal(amount)
            } catch (e: NumberFormatException) {
                return amount
            }

            return when {
                // Show scientific notation if too large or very small
                bigDecimal.abs() >= BigDecimal("1E12") ||
                        (bigDecimal.compareTo(BigDecimal.ZERO) != 0 && bigDecimal.abs() < BigDecimal("1E-6")) -> {
                    val sci = bigDecimal.round(MathContext(3)).toEngineeringString()
                    if (sci.length > 14) sci.take(14) else sci
                }
                bigDecimal.scale() <= 0 -> {
                    // It's a whole number
                    bigDecimal.toBigInteger().toString().take(14)
                }
                else -> {
                    // Decimal number with max 4 decimal places
                    val rounded = bigDecimal.setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()
                    if (rounded.length > 14) rounded.take(14) else rounded
                }
            }
        }
    }
}
