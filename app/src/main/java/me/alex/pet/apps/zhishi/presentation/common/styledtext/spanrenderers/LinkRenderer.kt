package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import me.alex.pet.apps.zhishi.domain.common.Link
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.androidspans.LinkSpan

class LinkRenderer(private val onClickDelegate: (Long) -> Unit) : SpanRenderer<Link> {

    override fun convertToAndroidSpans(spans: List<Link>): List<PositionAwareSpan> {
        return spans.map { convertToAndroidSpan(it) }
    }

    private fun convertToAndroidSpan(span: Link): PositionAwareSpan {
        return PositionAwareSpan(
                LinkSpan(span.ruleId, onClickDelegate),
                span.start,
                span.end
        )
    }
}