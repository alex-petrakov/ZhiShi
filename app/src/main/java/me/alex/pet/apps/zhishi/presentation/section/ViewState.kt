package me.alex.pet.apps.zhishi.presentation.section

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class ViewState(val ruleNumbersRange: LongRange, val elements: List<DisplayableElement>)

sealed class DisplayableElement {
    data class Heading(val content: StyledText) : DisplayableElement()
    data class Rule(val id: Long, val content: StyledText) : DisplayableElement()
}