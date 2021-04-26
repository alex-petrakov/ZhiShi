package me.alex.pet.apps.zhishi.presentation.common.extensions

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt

@ColorInt
fun Resources.Theme.resolveColorAttr(resId: Int): Int {
    val typedValue = TypedValue()
    val result = resolveAttribute(resId, typedValue, true)
    check(result) { "Attribute with ID $resId was not found or is not valid" }
    check(typedValue.isColor) { "Attribute with ID $resId does not represent a color" }
    return typedValue.data
}

private val TypedValue.isColor get() = type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT