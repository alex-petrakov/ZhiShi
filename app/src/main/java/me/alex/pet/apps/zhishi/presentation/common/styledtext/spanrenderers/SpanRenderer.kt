package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.text.TextPaint
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

interface SpanRenderer<T> {
    fun convertToSpans(elements: List<T>, textPaint: TextPaint): List<PositionAwareSpan>
}