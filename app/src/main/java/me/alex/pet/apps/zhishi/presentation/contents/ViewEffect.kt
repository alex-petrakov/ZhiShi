package me.alex.pet.apps.zhishi.presentation.contents

sealed class ViewEffect {
    data class NavigateToSection(val sectionId: Long) : ViewEffect()
    object NavigateToSearch : ViewEffect()
}