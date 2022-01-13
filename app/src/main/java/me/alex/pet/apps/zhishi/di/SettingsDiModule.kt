package me.alex.pet.apps.zhishi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.alex.pet.apps.zhishi.data.settings.ThemeManagerImpl
import me.alex.pet.apps.zhishi.domain.settings.ThemeManager

@Module
@InstallIn(SingletonComponent::class)
interface SettingsDiModule {

    @Binds
    fun bindSettingsRepository(themeManager: ThemeManagerImpl): ThemeManager
}