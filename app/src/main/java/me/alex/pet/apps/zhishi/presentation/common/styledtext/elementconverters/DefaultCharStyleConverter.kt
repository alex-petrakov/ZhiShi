package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.content.res.Resources
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.common.CharacterStyle
import me.alex.pet.apps.zhishi.domain.common.CharacterStyleType
import me.alex.pet.apps.zhishi.presentation.common.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.ColoredUnderlineSpan

class DefaultCharStyleConverter(private val theme: Resources.Theme) : ElementConverter<CharacterStyle> {

    private val emphasisColor by lazy { theme.resolveColorAttr(R.attr.colorTextEmphasis) }

    private val underlineColor by lazy { theme.resolveColorAttr(R.attr.colorMisspelledTextUnderline) }

    override fun convertToSpan(element: CharacterStyle, textPaint: TextPaint): PositionAwareSpan? {
        val span = when (element.type) {
            CharacterStyleType.EMPHASIS -> ForegroundColorSpan(emphasisColor)
            CharacterStyleType.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterStyleType.MISSPELL -> ColoredUnderlineSpan(underlineColor)
        }
        return PositionAwareSpan(span, element.start, element.end)
    }
}