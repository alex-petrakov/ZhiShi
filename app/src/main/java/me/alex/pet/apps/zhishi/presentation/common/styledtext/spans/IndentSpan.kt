package me.alex.pet.apps.zhishi.presentation.common.styledtext.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.Px
import kotlin.math.roundToInt

data class IndentSpan(val level: Int) : LeadingMarginSpan {

    @Px
    private var indentWidth: Int = 0

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
        indentWidth = p.measureText(indentStepLengthSample).roundToInt()
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return indentWidth * level
    }
}

private const val indentStepLengthSample = "   "

private const val minLevel = 1
private const val maxLevel = 5