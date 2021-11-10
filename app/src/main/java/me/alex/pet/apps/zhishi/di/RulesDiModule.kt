package me.alex.pet.apps.zhishi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.alex.pet.apps.zhishi.data.rules.RulesDataStore
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository

@Module
@InstallIn(SingletonComponent::class)
interface RulesDiModule {

    @Binds
    fun rulesRepository(rulesDataStore: RulesDataStore): RulesRepository
}