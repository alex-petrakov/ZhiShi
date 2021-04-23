package me.alex.pet.apps.zhishi.presentation.rules.rule

sealed class ViewEffect {
    data class NavigateToRule(val ruleId: Long) : ViewEffect()
}