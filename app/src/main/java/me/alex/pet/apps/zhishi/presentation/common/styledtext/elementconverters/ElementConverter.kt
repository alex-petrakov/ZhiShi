package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.text.TextPaint
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

interface ElementConverter<T> {
    fun convertToSpan(element: T, textPaint: TextPaint): PositionAwareSpan?
}