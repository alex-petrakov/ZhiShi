package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.StyleSpan
import me.alex.pet.apps.zhishi.domain.common.CharacterAppearance
import me.alex.pet.apps.zhishi.domain.common.CharacterSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

class BasicCharStyleConverter : ElementConverter<CharacterSpan> {

    override fun convertToSpan(element: CharacterSpan, textPaint: TextPaint): PositionAwareSpan? {
        val span = when (element.appearance) {
            CharacterAppearance.EMPHASIS -> StyleSpan(Typeface.ITALIC)
            CharacterAppearance.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterAppearance.MISSPELL -> null
        }
        return span?.let { PositionAwareSpan(span, element.start, element.end) }
    }
}