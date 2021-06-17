package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import android.text.TextPaint
import me.alex.pet.apps.zhishi.domain.common.Link
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans.LinkSpan

class LinkRenderer(private val onClickDelegate: (Long) -> Unit) : SpanRenderer<Link> {

    override fun convertToSpans(elements: List<Link>, textPaint: TextPaint): List<PositionAwareSpan> {
        return elements.map { convertToSpan(it, textPaint) }
    }

    private fun convertToSpan(element: Link, textPaint: TextPaint): PositionAwareSpan {
        return PositionAwareSpan(
                LinkSpan(element.ruleId, onClickDelegate),
                element.start,
                element.end
        )
    }
}