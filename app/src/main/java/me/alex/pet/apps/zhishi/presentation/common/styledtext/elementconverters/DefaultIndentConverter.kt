package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.Px
import me.alex.pet.apps.zhishi.domain.Indent
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import kotlin.math.roundToInt

class DefaultIndentConverter : ElementConverter<Indent> {

    override fun convertToSpan(element: Indent): PositionAwareSpan? {
        return PositionAwareSpan(IndentSpan(element.level), element.start, element.end)
    }
}

data class IndentSpan(private val level: Int) : LeadingMarginSpan {

    @Px
    private var step: Int = 0

    init {
        require(level in minLevel..maxLevel) {
            "Level must be in [$minLevel, $maxLevel], but it was $level"
        }
    }

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
        step = p.measureText(indentStepLengthSample).roundToInt()
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return step * level
    }
}

private const val indentStepLengthSample = "   "

private const val minLevel = 1
private const val maxLevel = 5