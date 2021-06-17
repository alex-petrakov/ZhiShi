package me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.Px

data class IndentSpan(@Px val stepWidth: Int, val level: Int) : LeadingMarginSpan {

    @Px
    private val indentWidth = stepWidth * level

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
        // Do nothing
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return indentWidth
    }
}

private const val minLevel = 1
private const val maxLevel = 5