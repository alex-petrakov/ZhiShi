package me.alex.pet.apps.zhishi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.alex.pet.apps.zhishi.data.sections.SectionsDataStore
import me.alex.pet.apps.zhishi.domain.sections.SectionsRepository

@Module
@InstallIn(SingletonComponent::class)
interface SectionsDiModule {

    @Binds
    fun sectionsRepository(sectionsDataStore: SectionsDataStore): SectionsRepository
}