package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.content.res.Resources
import android.text.TextPaint
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.common.ParagraphAppearance
import me.alex.pet.apps.zhishi.domain.common.ParagraphSpan
import me.alex.pet.apps.zhishi.presentation.common.extensions.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.IndentSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.QuotationSpan
import kotlin.math.roundToInt

class DefaultParagraphStyleConverter(theme: Resources.Theme) : ElementConverter<ParagraphSpan> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val stripeWidth = 2.dp(theme.resources)

    override fun convertToSpan(element: ParagraphSpan, textPaint: TextPaint): PositionAwareSpan? {
        return when (element) {
            is ParagraphSpan.Indent -> convertIndentToSpan(element, textPaint)
            is ParagraphSpan.HangingIndent -> TODO("Not implemented")
            is ParagraphSpan.Style -> convertAppearanceToSpan(element, textPaint)
        }
    }

    private fun convertIndentToSpan(element: ParagraphSpan.Indent, textPaint: TextPaint): PositionAwareSpan {
        // TODO: Measuring the text each time is expensive. Consider adding some kind of caching.
        val indentStepWidth = textPaint.measureText(indentStepLengthSample).roundToInt()
        return PositionAwareSpan(IndentSpan(indentStepWidth, element.level), element.start, element.end)
    }

    private fun convertAppearanceToSpan(element: ParagraphSpan.Style, textPaint: TextPaint): PositionAwareSpan? {
        val span = when (element.appearance) {
            ParagraphAppearance.QUOTE -> {
                // TODO: Measuring the text each time is expensive. Consider adding some kind of caching.
                val gapWidth = textPaint.measureText(gapLengthSample).roundToInt()
                QuotationSpan(stripeColor, stripeWidth, gapWidth)
            }
            ParagraphAppearance.FOOTNOTE -> null
        }
        return span?.let { PositionAwareSpan(span, element.start, element.end) }
    }
}

private const val indentStepLengthSample = "   "

private const val gapLengthSample = "  "

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}