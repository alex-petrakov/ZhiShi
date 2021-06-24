package me.alex.pet.apps.zhishi.presentation.section

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class ViewState(val ruleNumbersRange: LongRange, val listItems: List<DisplayableItem>)

sealed class DisplayableItem {
    data class Heading(val content: StyledText) : DisplayableItem()
    data class Rule(val id: Long, val content: StyledText) : DisplayableItem()
}