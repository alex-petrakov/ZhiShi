package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import me.alex.pet.apps.zhishi.R
import me.alex.pet.apps.zhishi.domain.ParagraphStyle
import me.alex.pet.apps.zhishi.presentation.common.resolveColorAttr
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import kotlin.math.roundToInt

class DefaultParagraphStyleConverter(theme: Resources.Theme) : ElementConverter<ParagraphStyle> {

    private val stripeColor by lazy { theme.resolveColorAttr(R.attr.colorPrimary) }

    private val stripeWidth = 2.dp(theme.resources)

    override fun convertToSpan(element: ParagraphStyle): PositionAwareSpan? {
        return PositionAwareSpan(QuotationSpan(stripeColor, stripeWidth), element.start, element.end)
    }
}

private fun Int.dp(resources: Resources): Int {
    return this * (resources.displayMetrics.density).roundToInt()
}

data class QuotationSpan(
        @ColorInt val color: Int,
        @Px val stripeWidth: Int
) : LeadingMarginSpan {

    init {
        require(stripeWidth > 0) { "Stripe width must be > 0, but it was $stripeWidth" }
    }

    @Px
    private var gapWidth: Int = 0

    override fun getLeadingMargin(first: Boolean) = stripeWidth + gapWidth

    override fun drawLeadingMargin(
            c: Canvas,
            p: Paint,
            x: Int,
            dir: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence,
            start: Int,
            end: Int,
            first: Boolean,
            layout: Layout
    ) {
        gapWidth = p.measureText(gapLengthSample).roundToInt()

        val oldStyle = p.style
        val oldAlpha = p.alpha
        val oldColor = p.color

        p.style = Paint.Style.FILL
        p.color = color
        p.alpha = 255

        c.drawRect(x.toFloat(), top.toFloat(), x + dir * stripeWidth.toFloat(), bottom.toFloat(), p)

        p.style = oldStyle
        p.alpha = oldAlpha
        p.color = oldColor
    }
}

private const val gapLengthSample = "  "