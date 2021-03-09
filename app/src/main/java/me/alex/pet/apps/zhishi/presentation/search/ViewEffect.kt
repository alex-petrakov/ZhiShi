package me.alex.pet.apps.zhishi.presentation.search

sealed class ViewEffect {
    data class NavigateToRule(val ruleId: Long) : ViewEffect()
}