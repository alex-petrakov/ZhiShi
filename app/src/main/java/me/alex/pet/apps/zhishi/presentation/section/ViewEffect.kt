package me.alex.pet.apps.zhishi.presentation.section

import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay

sealed class ViewEffect {
    data class NavigateToRule(val rulesToDisplay: RulesToDisplay) : ViewEffect()
}