package me.alex.pet.apps.zhishi.domain.settings

import kotlinx.coroutines.flow.Flow

interface ThemeManager {

    fun getThemeSwitchingModeFlow(): Flow<ThemeSwitchingMode>

    var themeSwitchingMode: ThemeSwitchingMode
}