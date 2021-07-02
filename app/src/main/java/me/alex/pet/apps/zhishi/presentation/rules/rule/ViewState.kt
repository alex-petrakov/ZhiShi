package me.alex.pet.apps.zhishi.presentation.rules.rule

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class ViewState(
        val ruleContent: StyledText,
        val sectionButtonIsVisible: Boolean,
        val sectionName: StyledText
)