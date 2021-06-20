package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.content.res.Resources
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.LeadingMarginSpan
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.common.ParagraphAppearance
import me.alex.pet.apps.zhishi.domain.common.ParagraphSpan
import me.alex.pet.apps.zhishi.presentation.common.extensions.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans.QuotationSpan
import kotlin.math.roundToInt

class ParagraphSpanRenderer(theme: Resources.Theme, private val textPaint: TextPaint) : SpanRenderer<ParagraphSpan> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val indentFirstStepWidth = textPaint.measureText(indentFirstStepSample).roundToInt()

    private val indentStepWidth = textPaint.measureText(indentStepSample).roundToInt()

    private val minHangingIndentWidth = textPaint.measureText(minHangingIndentSample).roundToInt()

    private val quoteStripeWidth = 2.dp(theme.resources)

    private val quoteBorderGapWidth = textPaint.measureText(quoteGapSample).roundToInt()

    private val footnoteTextSize = 14.sp(theme.resources)

    override fun convertToSpans(elements: List<ParagraphSpan>): List<PositionAwareSpan> {
        return elements.map { convertToSpan(it) }
    }

    private fun convertToSpan(element: ParagraphSpan): PositionAwareSpan {
        return when (element) {
            is ParagraphSpan.Indent -> convertIndentToSpan(element)
            is ParagraphSpan.Style -> convertAppearanceToSpan(element)
        }
    }

    private fun convertIndentToSpan(element: ParagraphSpan.Indent): PositionAwareSpan {
        val restLinesIndent = when (element.level) {
            1 -> indentFirstStepWidth
            else -> indentFirstStepWidth + indentStepWidth * (element.level - 1)
        }
        val hangingTextWidth = textPaint.measureText(element.hangingText)
        val firstLineIndent = when (element.level) {
            0 -> minHangingIndentWidth - hangingTextWidth
            else -> restLinesIndent - hangingTextWidth
        }.roundToInt()
        val span = LeadingMarginSpan.Standard(firstLineIndent, restLinesIndent)
        return PositionAwareSpan(span, element.start, element.end)
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
        // AbsoluteSizeSpan must be also applied to the newline character at the end of the line,
        // otherwise the last line in a paragraph might have incorrect line height.
        val end = when (element.appearance) {
            ParagraphAppearance.QUOTE -> element.end
            ParagraphAppearance.FOOTNOTE -> element.end + 1
        }
        return PositionAwareSpan(span, element.start, end)
    }
}

private const val indentFirstStepSample = "      "

private const val indentStepSample = "   "

private const val minHangingIndentSample = "MM. "

private const val quoteGapSample = "  "

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}

private fun Int.sp(resources: Resources): Int {
    return this * (resources.displayMetrics.scaledDensity).roundToInt()
}