package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.content.res.Resources
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.ParagraphStyle
import me.alex.pet.apps.zhishi.domain.ParagraphStyleType
import me.alex.pet.apps.zhishi.presentation.common.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.QuotationSpan
import kotlin.math.roundToInt

class DefaultParagraphStyleConverter(theme: Resources.Theme) : ElementConverter<ParagraphStyle> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val stripeWidth = 2.dp(theme.resources)

    override fun convertToSpan(element: ParagraphStyle): PositionAwareSpan? {
        val span = when (element.type) {
            ParagraphStyleType.QUOTE -> QuotationSpan(stripeColor, stripeWidth)
            ParagraphStyleType.FOOTNOTE -> null
        }
        return span?.let { PositionAwareSpan(span, element.start, element.end) }
    }
}

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}