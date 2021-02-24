package me.alex.pet.apps.zhishi.di

import me.alex.pet.apps.zhishi.data.RulesDataStore
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.presentation.contents.ContentsViewModel
import me.alex.pet.apps.zhishi.presentation.rule.RuleViewModel
import me.alex.pet.apps.zhishi.presentation.section.SectionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<RulesRepository> { RulesDataStore() }
    viewModel { ContentsViewModel(get()) }
    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get()) }
    viewModel { (ruleId: Long) -> RuleViewModel(ruleId, get()) }
}