package me.alex.pet.apps.zhishi.di

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.alex.pet.apps.zhishi.Database
import me.alex.pet.apps.zhishi.data.RulesDataStore
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.presentation.contents.ContentsViewModel
import me.alex.pet.apps.zhishi.presentation.rule.RuleViewModel
import me.alex.pet.apps.zhishi.presentation.section.SectionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, androidContext(), "rules.db") }
    single { Database(get()) }

    single<RulesRepository> { RulesDataStore(get<Database>().rulesQueries) }

    viewModel { ContentsViewModel(get()) }
    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get()) }
    viewModel { (ruleId: Long) -> RuleViewModel(ruleId, get()) }
}