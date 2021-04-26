package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.content.res.Resources
import android.text.TextPaint
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.common.ParagraphStyle
import me.alex.pet.apps.zhishi.domain.common.ParagraphStyleType
import me.alex.pet.apps.zhishi.presentation.common.extensions.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.QuotationSpan
import kotlin.math.roundToInt

class DefaultParagraphStyleConverter(theme: Resources.Theme) : ElementConverter<ParagraphStyle> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val stripeWidth = 2.dp(theme.resources)

    override fun convertToSpan(element: ParagraphStyle, textPaint: TextPaint): PositionAwareSpan? {
        val span = when (element.type) {
            ParagraphStyleType.QUOTE -> {
                // TODO: Measuring the text each time is expensive. Consider adding some kind of caching.
                val gapWidth = textPaint.measureText(gapLengthSample).roundToInt()
                QuotationSpan(stripeColor, stripeWidth, gapWidth)
            }
            ParagraphStyleType.FOOTNOTE -> null
        }
        return span?.let { PositionAwareSpan(span, element.start, element.end) }
    }
}

private const val gapLengthSample = "  "

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}