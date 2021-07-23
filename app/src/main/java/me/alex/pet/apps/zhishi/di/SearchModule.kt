package me.alex.pet.apps.zhishi.di

import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.search.SearchProvider
import me.alex.pet.apps.zhishi.domain.search.SearchRepository
import me.alex.pet.apps.zhishi.domain.search.SearchRules
import me.alex.pet.apps.zhishi.domain.search.stemming.Stemmer
import me.alex.pet.apps.zhishi.presentation.search.SearchViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    single<SearchRepository> { SearchProvider(get<Database>().ruleQueries) }

    factory { Stemmer() }

    factory { SearchRules(get(), get()) }

    viewModel { SearchViewModel(get(), get(), get()) }
}