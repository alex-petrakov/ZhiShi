package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import me.alex.pet.apps.zhishi.domain.Link
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.LinkSpan

class DefaultLinkConverter(private val onClickDelegate: (Long) -> Unit) : ElementConverter<Link> {
    override fun convertToSpan(element: Link): PositionAwareSpan? {
        return PositionAwareSpan(
                LinkSpan(element.ruleId, onClickDelegate),
                element.start,
                element.end
        )
    }
}