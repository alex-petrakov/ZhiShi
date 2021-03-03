package me.alex.pet.apps.zhishi.presentation.common.styledtext.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px

data class QuotationSpan(
        @ColorInt val color: Int,
        @Px val stripeWidth: Int,
        @Px val gapWidth: Int
) : LeadingMarginSpan {

    init {
        require(stripeWidth > 0) { "Stripe width must be > 0, but it was $stripeWidth" }
        require(gapWidth > 0) { "Gap width must be > 0, but it was $gapWidth" }
    }

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