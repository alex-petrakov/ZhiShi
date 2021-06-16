package me.alex.pet.apps.zhishi.domain.common

data class CharacterSpan(val start: Int, val end: Int, val appearance: CharacterAppearance)

enum class CharacterAppearance {
    EMPHASIS,
    STRONG_EMPHASIS,
    MISSPELL
}