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

    private val indentStepWidth = textPaint.measureText(indentStepSample).roundToInt()

    private val quoteStripeWidth = 2.dp(theme.resources)

    private val quoteGapWidth = textPaint.measureText(quoteGapSample).roundToInt()

    private val footnoteTextSize = 15.sp(theme.resources)

    private val footnoteTextPaint = TextPaint().apply {
        set(textPaint)
        textSize = footnoteTextSize.toFloat()
    }

    override fun convertToAndroidSpans(spans: List<ParagraphSpan>): List<PositionAwareSpan> {
        return spans.flatMap { convertToAndroidSpans(it) }
    }

    private fun convertToAndroidSpans(span: ParagraphSpan): List<PositionAwareSpan> {
        val outerIndentSpan = convertToOuterIndentSpan(span)
        val appearanceSpans = convertToAppearanceSpans(span)
        val innerIndentSpan = convertToInnerIndentSpan(span)
        return listOfNotNull(outerIndentSpan) + appearanceSpans + listOfNotNull(innerIndentSpan)
    }

    private fun convertToOuterIndentSpan(span: ParagraphSpan): PositionAwareSpan? {
        if (!span.hasOuterIndent) {
            return null
        }
        val hangingText = when {
            span.hasInnerIndent -> ""
            else -> span.indent.hangingText
        }
        return newIndent(
                span.start,
                span.end,
                span.indent.outer,
                hangingText,
                getTextPaintFor(span)
        )
    }

    private fun convertToInnerIndentSpan(span: ParagraphSpan): PositionAwareSpan? {
        if (!span.hasInnerIndent) {
            return null
        }
        return newIndent(
                span.start,
                span.end,
                span.indent.inner,
                span.indent.hangingText,
                getTextPaintFor(span)
        )
    }

    private fun getTextPaintFor(span: ParagraphSpan): TextPaint {
        return when {
            span.isFootnote -> footnoteTextPaint
            else -> textPaint
        }
    }

    private val ParagraphSpan.isFootnote: Boolean
        get() {
            return this.appearance == ParagraphAppearance.FOOTNOTE ||
                    this.appearance == ParagraphAppearance.FOOTNOTE_QUOTE
        }

    private val ParagraphSpan.hasInnerIndent get() = indent.inner > 0

    private val ParagraphSpan.hasOuterIndent get() = indent.outer > 0

    private fun newIndent(
            start: Int,
            end: Int,
            level: Int,
            hangingText: String,
            textPaint: TextPaint
    ): PositionAwareSpan {
        require(level > 0)
        val restLinesIndent = indentStepWidth * level
        val hangingTextWidth = textPaint.measureText(hangingText)
        val firstLineIndent = (restLinesIndent - hangingTextWidth).roundToInt()
        val leadingMarginSpan = LeadingMarginSpan.Standard(firstLineIndent, restLinesIndent)
        return PositionAwareSpan(leadingMarginSpan, start, end)
    }

    private fun convertToAppearanceSpans(span: ParagraphSpan): List<PositionAwareSpan> {
        return when (span.appearance) {
            ParagraphAppearance.NORMAL -> emptyList()
            ParagraphAppearance.FOOTNOTE -> listOf(newFootnote(span.start, span.end))
            ParagraphAppearance.QUOTE -> listOf(newQuote(span.start, span.end))
            ParagraphAppearance.FOOTNOTE_QUOTE -> newFootnoteQuote(span.start, span.end)
        }
    }

    private fun newFootnote(start: Int, end: Int): PositionAwareSpan {
        val footnoteSpan = AbsoluteSizeSpan(footnoteTextSize, false)
        return PositionAwareSpan(footnoteSpan, start, end)
    }

    private fun newQuote(start: Int, end: Int): PositionAwareSpan {
        val quotationSpan = QuotationSpan(stripeColor, quoteStripeWidth, quoteGapWidth)
        return PositionAwareSpan(quotationSpan, start, end)
    }

    private fun newFootnoteQuote(start: Int, end: Int): List<PositionAwareSpan> {
        return listOf(newFootnote(start, end), newQuote(start, end))
    }
}

private const val indentStepSample = "     "

private const val quoteGapSample = "  "

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}

private fun Int.sp(resources: Resources): Int {
    return this * (resources.displayMetrics.scaledDensity).roundToInt()
}