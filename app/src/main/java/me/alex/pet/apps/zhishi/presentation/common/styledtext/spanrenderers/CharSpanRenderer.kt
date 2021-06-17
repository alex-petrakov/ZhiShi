package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.content.res.Resources
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.common.CharacterAppearance
import me.alex.pet.apps.zhishi.domain.common.CharacterSpan
import me.alex.pet.apps.zhishi.presentation.common.extensions.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans.ColoredUnderlineSpan

class CharSpanRenderer(private val theme: Resources.Theme) : SpanRenderer<CharacterSpan> {

    private val emphasisColor by lazy { theme.resolveColorAttr(R.attr.colorTextEmphasis) }

    private val underlineColor by lazy { theme.resolveColorAttr(R.attr.colorMisspelledTextUnderline) }

    override fun convertToSpans(elements: List<CharacterSpan>): List<PositionAwareSpan> {
        return elements.map { convertToSpan(it) }
    }

    private fun convertToSpan(element: CharacterSpan): PositionAwareSpan {
        val span = when (element.appearance) {
            CharacterAppearance.EMPHASIS -> ForegroundColorSpan(emphasisColor)
            CharacterAppearance.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterAppearance.MISSPELL -> ColoredUnderlineSpan(underlineColor)
        }
        return PositionAwareSpan(span, element.start, element.end)
    }
}