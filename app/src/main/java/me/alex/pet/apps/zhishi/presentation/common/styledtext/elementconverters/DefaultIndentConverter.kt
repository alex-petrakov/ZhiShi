package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import me.alex.pet.apps.zhishi.domain.Indent
import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan
import me.alex.pet.apps.zhishi.presentation.common.styledtext.spans.IndentSpan

class DefaultIndentConverter : ElementConverter<Indent> {

    override fun convertToSpan(element: Indent): PositionAwareSpan? {
        return PositionAwareSpan(IndentSpan(element.level), element.start, element.end)
    }
}