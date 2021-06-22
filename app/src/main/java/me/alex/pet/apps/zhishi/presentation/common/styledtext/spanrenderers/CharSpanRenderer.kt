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

    override fun convertToAndroidSpans(spans: List<CharacterSpan>): List<PositionAwareSpan> {
        return spans.map { convertToAndroidSpan(it) }
    }

    private fun convertToAndroidSpan(span: CharacterSpan): PositionAwareSpan {
        val androidSpan = when (span.appearance) {
            CharacterAppearance.EMPHASIS -> ForegroundColorSpan(emphasisColor)
            CharacterAppearance.STRONG_EMPHASIS -> StyleSpan(Typeface.BOLD)
            CharacterAppearance.MISSPELL -> ColoredUnderlineSpan(underlineColor)
        }
        return PositionAwareSpan(androidSpan, span.start, span.end)
    }
}