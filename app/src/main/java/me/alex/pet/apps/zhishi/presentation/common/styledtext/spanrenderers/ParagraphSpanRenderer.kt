package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.content.res.Resources
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.common.ParagraphAppearance
import me.alex.pet.apps.zhishi.domain.common.ParagraphSpan
import me.alex.pet.apps.zhishi.presentation.common.extensions.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans.IndentSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans.QuotationSpan
import kotlin.math.roundToInt

class ParagraphSpanRenderer(theme: Resources.Theme, textPaint: TextPaint) : SpanRenderer<ParagraphSpan> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val indentStepWidth = textPaint.measureText(indentStepLengthSample).roundToInt()

    private val quoteStripeWidth = 2.dp(theme.resources)

    private val footnoteTextSize = 14.sp(theme.resources)

    private val quoteBorderGapWidth = textPaint.measureText(gapLengthSample).roundToInt()

    override fun convertToSpans(elements: List<ParagraphSpan>): List<PositionAwareSpan> {
        return elements.map { convertToSpan(it) }
    }

    private fun convertToSpan(element: ParagraphSpan): PositionAwareSpan {
        return when (element) {
            is ParagraphSpan.Indent -> convertIndentToSpan(element)
            is ParagraphSpan.HangingIndent -> TODO("Not implemented")
            is ParagraphSpan.Style -> convertAppearanceToSpan(element)
        }
    }

    private fun convertIndentToSpan(element: ParagraphSpan.Indent): PositionAwareSpan {
        return PositionAwareSpan(IndentSpan(indentStepWidth, element.level), element.start, element.end)
    }

    private fun convertAppearanceToSpan(element: ParagraphSpan.Style): PositionAwareSpan {
        val span: Any = when (element.appearance) {
            ParagraphAppearance.QUOTE -> QuotationSpan(
                    stripeColor,
                    quoteStripeWidth,
                    quoteBorderGapWidth
            )
            ParagraphAppearance.FOOTNOTE -> AbsoluteSizeSpan(footnoteTextSize, false)
        }
        return PositionAwareSpan(span, element.start, element.end)
    }
}

private const val indentStepLengthSample = "   "

private const val gapLengthSample = "  "

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}

private fun Int.sp(resources: Resources): Int {
    return this * (resources.displayMetrics.scaledDensity).roundToInt()
}