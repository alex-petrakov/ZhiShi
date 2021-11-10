package me.alex.pet.apps.zhishi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.alex.pet.apps.zhishi.data.search.SearchProvider
import me.alex.pet.apps.zhishi.domain.search.SearchRepository

@Module
@InstallIn(SingletonComponent::class)
interface SearchDiModule {

    @Binds
    fun searchRepository(searchProvider: SearchProvider): SearchRepository
}