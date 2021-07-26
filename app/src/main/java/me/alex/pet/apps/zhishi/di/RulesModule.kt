package me.alex.pet.apps.zhishi.di

import com.squareup.moshi.Moshi
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.rules.RulesDataStore
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import me.alex.pet.apps.zhishi.presentation.rules.RulesViewModel
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val rulesModule = module {

    single { createRulesRepository(get(), get()) }

    viewModel { (rulesToDisplay: RulesToDisplay) -> RulesViewModel(rulesToDisplay, get()) }
    viewModel { (ruleId: Long, displaySectionButton: Boolean) ->
        RuleViewModel(ruleId, displaySectionButton, get(), get())
    }
}

private fun createRulesRepository(db: Database, moshi: Moshi): RulesRepository {
    return RulesDataStore(db.ruleQueries, moshi)
}