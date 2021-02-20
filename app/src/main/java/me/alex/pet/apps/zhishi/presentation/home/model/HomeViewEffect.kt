package me.alex.pet.apps.zhishi.presentation.home.model

sealed class HomeViewEffect {
    data class NavigateToSection(val sectionId: Long) : HomeViewEffect()
}