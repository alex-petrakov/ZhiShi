package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.StyleSpan
import me.alex.pet.apps.zhishi.domain.common.CharacterAppearance
import me.alex.pet.apps.zhishi.domain.common.CharacterSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

class BasicCharSpanRenderer : SpanRenderer<CharacterSpan> {

    override fun convertToSpans(elements: List<CharacterSpan>, textPaint: TextPaint): List<PositionAwareSpan> {
        return elements.mapNotNull { convertToSpan(it, textPaint) }
    }

    private fun convertToSpan(element: CharacterSpan, textPaint: TextPaint): PositionAwareSpan? {
        val span = when (element.appearance) {
            CharacterAppearance.EMPHASIS -> StyleSpan(Typeface.ITALIC)
            CharacterAppearance.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterAppearance.MISSPELL -> null
        }
        return span?.let { PositionAwareSpan(span, element.start, element.end) }
    }
}