package me.alex.pet.apps.zhishi.presentation.common

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spanned
import android.util.AttributeSet
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.ColoredUnderlineSpan

class FancyTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    @Px
    private val customUnderlineThickness = 1.dp(resources)

    private val underlinePaint = Paint().apply {
        color = paint.color
        style = Paint.Style.FILL
        strokeWidth = customUnderlineThickness
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentText = text
        if (currentText is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                renderSpans(currentText, canvas)
            }
        }
    }

    private fun renderSpans(currentText: Spanned, canvas: Canvas) {
        val spans = currentText.getSpans(0, currentText.length, ColoredUnderlineSpan::class.java)
        for (span in spans) {
            renderSpan(span, currentText, canvas)
        }
    }

    private fun renderSpan(span: ColoredUnderlineSpan, text: Spanned, canvas: Canvas) {
        underlinePaint.color = span.color

        val spanStart = text.getSpanStart(span)
        val spanEnd = text.getSpanEnd(span)

        val startLine = layout.getLineForOffset(spanStart)
        val endLine = layout.getLineForOffset(spanEnd)

        for (line in startLine..endLine) {
            val descent = layout.getLineBaseline(line) + layout.getLineDescent(line).toFloat()
            val underlineStartOffset = when (line) {
                startLine -> spanStart
                else -> layout.getLineStart(line)
            }
            val underlineEndOffset = when (line) {
                endLine -> spanEnd
                else -> layout.getLineVisibleEnd(line)
            }
            val underlineStart = layout.getPrimaryHorizontal(underlineStartOffset)
            val underlineEnd = layout.getSecondaryHorizontal(underlineEndOffset)
            canvas.drawLine(underlineStart, descent, underlineEnd, descent, underlinePaint)
        }
    }
}

private fun Int.dp(resources: Resources): Float {
    return this * resources.displayMetrics.density
}