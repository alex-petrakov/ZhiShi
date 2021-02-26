package me.alex.pet.apps.zhishi.presentation.section

sealed class ViewEffect {
    data class NavigateToRule(val ruleId: Long) : ViewEffect()
}