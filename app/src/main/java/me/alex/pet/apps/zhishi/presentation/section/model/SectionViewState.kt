package me.alex.pet.apps.zhishi.presentation.section.model

import me.alex.pet.apps.zhishi.domain.StyledText

data class SectionViewState(val rulesRange: IntRange, val elements: List<DisplayableElement>)

sealed class DisplayableElement {
    data class Heading(val content: StyledText) : DisplayableElement()
    data class Rule(val id: Long, val number: Int, val content: StyledText) : DisplayableElement()
}