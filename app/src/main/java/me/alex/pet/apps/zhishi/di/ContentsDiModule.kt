package me.alex.pet.apps.zhishi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.alex.pet.apps.zhishi.data.contents.ContentsDataStore
import me.alex.pet.apps.zhishi.domain.contents.ContentsRepository

@Module
@InstallIn(SingletonComponent::class)
interface ContentsDiModule {

    @Binds
    fun contentsRepository(contentsDataStore: ContentsDataStore): ContentsRepository
}