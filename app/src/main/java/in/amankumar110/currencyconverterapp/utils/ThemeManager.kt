package `in`.amankumar110.currencyconverterapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat


class ThemeManager {
    companion object {
        fun getThemeColor(context: Context, attrResId: Int): Int {
            val typedValue = TypedValue()
            val theme: Resources.Theme = context.theme
            if (theme.resolveAttribute(attrResId, typedValue, true)) {
                if (typedValue.resourceId != 0) {
                    return ContextCompat.getColor(context, typedValue.resourceId)
                } else {
                    return typedValue.data
                }
            } else {
                // fallback color
                return Color.GRAY
            }
        }

    }
}