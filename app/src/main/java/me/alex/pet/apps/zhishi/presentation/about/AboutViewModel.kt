package me.alex.pet.apps.zhishi.presentation.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.alex.pet.apps.zhishi.domain.settings.ThemeManager
import me.alex.pet.apps.zhishi.domain.settings.ThemeSwitchingMode
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(private val themeManager: ThemeManager) : ViewModel() {

    val themeSwitchingMode = themeManager.getThemeSwitchingModeFlow().asLiveData()

    fun onChangeThemeSwitchingMode(desiredThemeSwitchingModel: ThemeSwitchingMode) {
        if (desiredThemeSwitchingModel == themeSwitchingMode.value) {
            return
        }
        themeManager.themeSwitchingMode = desiredThemeSwitchingModel
    }
}
