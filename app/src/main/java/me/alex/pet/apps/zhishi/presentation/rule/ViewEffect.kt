package me.alex.pet.apps.zhishi.presentation.rule

sealed class ViewEffect {
    data class NavigateToRule(val ruleId: Long) : ViewEffect()
}