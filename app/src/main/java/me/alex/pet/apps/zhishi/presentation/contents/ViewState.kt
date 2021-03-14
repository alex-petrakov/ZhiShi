package me.alex.pet.apps.zhishi.presentation.contents

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class ViewState(val contents: List<ContentsElement>)

sealed class ContentsElement {
    data class Part(val name: String) : ContentsElement()
    data class Chapter(val name: String) : ContentsElement()
    data class Section(val id: Long, val name: StyledText) : ContentsElement()
}