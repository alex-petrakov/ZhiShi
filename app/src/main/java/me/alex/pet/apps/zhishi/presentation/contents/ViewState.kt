package me.alex.pet.apps.zhishi.presentation.contents

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class ViewState(val listItems: List<DisplayableItem>)

sealed class DisplayableItem {
    data class Part(val name: String) : DisplayableItem()
    data class Chapter(val name: String) : DisplayableItem()
    data class Section(val id: Long, val name: StyledText) : DisplayableItem()
}