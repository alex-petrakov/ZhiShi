package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.StyleSpan
import me.alex.pet.apps.zhishi.domain.CharacterStyle
import me.alex.pet.apps.zhishi.domain.CharacterStyleType
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

class BasicCharStyleConverter : ElementConverter<CharacterStyle> {

    override fun convertToSpan(element: CharacterStyle, textPaint: TextPaint): PositionAwareSpan? {
        val span = when (element.type) {
            CharacterStyleType.EMPHASIS -> StyleSpan(Typeface.ITALIC)
            CharacterStyleType.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterStyleType.MISSPELL -> null
        }
        return span?.let { PositionAwareSpan(span, element.start, element.end) }
    }
}