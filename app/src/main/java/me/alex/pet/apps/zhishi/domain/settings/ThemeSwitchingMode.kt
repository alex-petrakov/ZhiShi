package me.alex.pet.apps.zhishi.domain.settings

enum class ThemeSwitchingMode(val id: Int) {
    FOLLOW_SYSTEM(0),
    ALWAYS_LIGHT(1),
    ALWAYS_DARK(2);

    companion object {
        fun from(id: Int): ThemeSwitchingMode? {
            return values().find { it.id == id }
        }
    }
}