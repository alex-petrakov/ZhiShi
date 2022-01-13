package me.alex.pet.apps.zhishi.data.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.alex.pet.apps.zhishi.BuildConfig
import me.alex.pet.apps.zhishi.domain.settings.ThemeManager
import me.alex.pet.apps.zhishi.domain.settings.ThemeSwitchingMode
import me.alex.pet.apps.zhishi.domain.settings.ThemeSwitchingMode.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class ThemeManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeManager {

    override var themeSwitchingMode: ThemeSwitchingMode
        get() = ThemeSwitchingMode.from(themeSwitchingModePref.get()) ?: FOLLOW_SYSTEM
        set(value) {
            themeSwitchingModePref.set(value.id)
            applySystemNightMode(value)
        }

    private val prefs = FlowSharedPreferences(
        context.getSharedPreferences(THEME_SETTINGS, Context.MODE_PRIVATE)
    )

    private val themeSwitchingModePref = prefs.getInt(
        PREF_THEME_SWITCHING_MODE,
        FOLLOW_SYSTEM.id
    )

    init {
        applySystemNightMode(themeSwitchingMode)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getThemeSwitchingModeFlow(): Flow<ThemeSwitchingMode> {
        return themeSwitchingModePref.asFlow()
            .map { ThemeSwitchingMode.from(it) ?: FOLLOW_SYSTEM }
    }

    private fun applySystemNightMode(themeSwitchingMode: ThemeSwitchingMode) {
        AppCompatDelegate.setDefaultNightMode(themeSwitchingMode.toSystemNightMode())
    }

    private fun ThemeSwitchingMode.toSystemNightMode(): Int {
        return when (this) {
            FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ALWAYS_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ALWAYS_DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    companion object {
        private const val THEME_SETTINGS = "${BuildConfig.APPLICATION_ID}.THEME_SETTINGS"

        private const val PREF_THEME_SWITCHING_MODE = "THEME_SWITCHING_MODE"
    }
}