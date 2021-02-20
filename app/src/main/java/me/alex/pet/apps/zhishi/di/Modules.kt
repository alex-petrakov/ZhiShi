package me.alex.pet.apps.zhishi.di

import me.alex.pet.apps.zhishi.data.RulesDataStore
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.presentation.home.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<RulesRepository> { RulesDataStore() }
    viewModel { HomeViewModel(get()) }
}