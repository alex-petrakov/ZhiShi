package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.graphics.Typeface
import android.text.style.StyleSpan
import me.alex.pet.apps.zhishi.domain.common.CharacterAppearance
import me.alex.pet.apps.zhishi.domain.common.CharacterSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

class BasicCharSpanRenderer : SpanRenderer<CharacterSpan> {

    override fun convertToAndroidSpans(spans: List<CharacterSpan>): List<PositionAwareSpan> {
        return spans.mapNotNull { convertToAndroidSpan(it) }
    }

    private fun convertToAndroidSpan(span: CharacterSpan): PositionAwareSpan? {
        val androidSpan = when (span.appearance) {
            CharacterAppearance.EMPHASIS -> StyleSpan(Typeface.ITALIC)
            CharacterAppearance.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterAppearance.MISSPELL -> null
        }
        return androidSpan?.let { PositionAwareSpan(it, span.start, span.end) }
    }
}