package me.alex.pet.apps.zhishi.presentation.home

sealed class ViewEffect {
    data class NavigateToSection(val sectionId: Long) : ViewEffect()
}