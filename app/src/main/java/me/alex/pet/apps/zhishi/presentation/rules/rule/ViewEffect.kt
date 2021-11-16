package me.alex.pet.apps.zhishi.presentation.rules.rule

sealed class ViewEffect {
    data class ShareRuleText(val text: String) : ViewEffect()
}