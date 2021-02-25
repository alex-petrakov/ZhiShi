package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import android.text.style.ClickableSpan
import android.view.View
import me.alex.pet.apps.zhishi.domain.Link
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

class DefaultLinkConverter(private val onClickDelegate: (Long) -> Unit) : ElementConverter<Link> {
    override fun convertToSpan(element: Link): PositionAwareSpan? {
        return PositionAwareSpan(
                LinkSpan(element.ruleId, onClickDelegate),
                element.start,
                element.end
        )
    }
}

data class LinkSpan(
        private val ruleId: Long,
        private val onClickDelegate: (Long) -> Unit
) : ClickableSpan() {
    override fun onClick(widget: View) {
        onClickDelegate(ruleId)
    }
}