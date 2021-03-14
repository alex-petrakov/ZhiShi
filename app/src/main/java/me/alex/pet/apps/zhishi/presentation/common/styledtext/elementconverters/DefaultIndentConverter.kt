package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.text.TextPaint
import me.alex.pet.apps.zhishi.domain.common.Indent
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.IndentSpan
import kotlin.math.roundToInt

class DefaultIndentConverter : ElementConverter<Indent> {

    override fun convertToSpan(element: Indent, textPaint: TextPaint): PositionAwareSpan? {
        // TODO: Measuring the text each time is expensive. Consider adding some kind of caching.
        val indentStepWidth = textPaint.measureText(indentStepLengthSample).roundToInt()
        return PositionAwareSpan(IndentSpan(indentStepWidth, element.level), element.start, element.end)
    }
}

private const val indentStepLengthSample = "   "