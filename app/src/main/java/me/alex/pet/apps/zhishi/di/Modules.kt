package me.alex.pet.apps.zhishi.di

import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.RulesDataStore
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.presentation.contents.ContentsViewModel
import me.alex.pet.apps.zhishi.presentation.rule.RuleViewModel
import me.alex.pet.apps.zhishi.presentation.search.SearchViewModel
import me.alex.pet.apps.zhishi.presentation.section.SectionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, androidContext(), "rules.db") }
    single { Database(get()) }

    single<Moshi> { Moshi.Builder().build() }

    single<RulesRepository> {
        val db = get<Database>()
        RulesDataStore(
                db.partQueries,
                db.chapterQueries,
                db.sectionQueries,
                db.ruleQueries,
                get()
        )
    }

    viewModel { ContentsViewModel(get()) }
    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get()) }
    viewModel { (ruleId: Long) -> RuleViewModel(ruleId, get()) }
    viewModel { SearchViewModel(get()) }
}