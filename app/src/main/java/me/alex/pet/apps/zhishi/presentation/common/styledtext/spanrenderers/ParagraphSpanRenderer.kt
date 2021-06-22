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

class ParagraphSpanRenderer(
        theme: Resources.Theme,
        private val textPaint: TextPaint
) : SpanRenderer<ParagraphSpan> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val indentFirstStepWidth = textPaint.measureText(indentFirstStepSample).roundToInt()

    private val indentStepWidth = textPaint.measureText(indentStepSample).roundToInt()

    private val minHangingIndentWidth = textPaint.measureText(minHangingIndentSample).roundToInt()

    private val quoteStripeWidth = 2.dp(theme.resources)

    private val quoteBorderGapWidth = textPaint.measureText(quoteGapSample).roundToInt()

    private val footnoteTextSize = 14.sp(theme.resources)

    override fun convertToAndroidSpans(spans: List<ParagraphSpan>): List<PositionAwareSpan> {
        val spansGroupedByStartIndices = spans.groupBy { it.start }
        return spans.flatMap { element ->
            val nextParagraphSpans = spansGroupedByStartIndices.getOrElse(element.end + 2) {
                emptyList()
            }
            convertToAndroidSpans(element, nextParagraphSpans)
        }
    }

    private fun convertToAndroidSpans(
            element: ParagraphSpan,
            nextParagraphSpans: List<ParagraphSpan>
    ): List<PositionAwareSpan> {
        return when (element) {
            is ParagraphSpan.Indent -> convertToAndroidSpans(element)
            is ParagraphSpan.Style -> convertToAndroidSpans(element, nextParagraphSpans)
        }
    }

    private fun convertToAndroidSpans(span: ParagraphSpan.Indent): List<PositionAwareSpan> {
        val restLinesIndent = indentFirstStepWidth + indentStepWidth * (span.level - 1)
        val hangingTextWidth = textPaint.measureText(span.hangingText)
        val firstLineIndent = when (span.level) {
            0 -> minHangingIndentWidth - hangingTextWidth
            else -> restLinesIndent - hangingTextWidth
        }.roundToInt()
        val androidSpan = LeadingMarginSpan.Standard(firstLineIndent, restLinesIndent)
        return listOf(PositionAwareSpan(androidSpan, span.start, span.end))
    }

    private fun convertToAndroidSpans(
            span: ParagraphSpan.Style,
            nextParagraphSpans: List<ParagraphSpan>
    ): List<PositionAwareSpan> {
        val mainAndroidSpan = when (span.appearance) {
            ParagraphAppearance.QUOTE -> PositionAwareSpan(newQuotationSpan(), span.start, span.end)
            ParagraphAppearance.FOOTNOTE -> PositionAwareSpan(
                    newFootnoteSpan(),
                    span.start,
                    span.end + 1
            )
        }
        val nextParagraphIsOfSameStyle = nextParagraphSpans.any { nextParaSpan ->
            nextParaSpan is ParagraphSpan.Style && nextParaSpan.appearance == span.appearance
        }
        val additionalAndroidSpan = when {
            nextParagraphIsOfSameStyle -> when (span.appearance) {
                ParagraphAppearance.QUOTE -> PositionAwareSpan(
                        newQuotationSpan(),
                        span.end + 1,
                        span.end + 1
                )
                ParagraphAppearance.FOOTNOTE -> PositionAwareSpan(
                        newFootnoteSpan(),
                        span.end + 1,
                        span.end + 2
                )
            }
            else -> null
        }
        return listOfNotNull(mainAndroidSpan, additionalAndroidSpan)
    }

    private fun newQuotationSpan(): QuotationSpan {
        return QuotationSpan(stripeColor, quoteStripeWidth, quoteBorderGapWidth)
    }

    private fun newFootnoteSpan(): AbsoluteSizeSpan {
        return AbsoluteSizeSpan(footnoteTextSize, false)
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