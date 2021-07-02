package me.alex.pet.apps.zhishi.di

import com.github.terrakok.cicerone.Cicerone
import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.common.CopyOpenHelperFactory
import me.alex.pet.apps.zhishi.data.contents.ContentsDataStore
import me.alex.pet.apps.zhishi.data.rules.RulesDataStore
import me.alex.pet.apps.zhishi.data.search.SearchProvider
import me.alex.pet.apps.zhishi.data.sections.SectionsDataStore
import me.alex.pet.apps.zhishi.domain.contents.ContentsRepository
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.domain.search.SearchRepository
import me.alex.pet.apps.zhishi.domain.search.SearchRules
import me.alex.pet.apps.zhishi.domain.search.stemming.Stemmer
import me.alex.pet.apps.zhishi.domain.sections.SectionsRepository
import me.alex.pet.apps.zhishi.presentation.contents.ContentsViewModel
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import me.alex.pet.apps.zhishi.presentation.rules.RulesViewModel
import me.alex.pet.apps.zhishi.presentation.rules.rule.RuleViewModel
import me.alex.pet.apps.zhishi.presentation.search.SearchViewModel
import me.alex.pet.apps.zhishi.presentation.section.SectionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SqlDriver> {
        val factory = CopyOpenHelperFactory("db/rules.db")
        AndroidSqliteDriver(Database.Schema, androidContext(), "rules.db", factory)
    }
    single { Database(get()) }

    single<Moshi> { Moshi.Builder().build() }

    single<ContentsRepository> {
        val db = get<Database>()
        ContentsDataStore(
                db.partQueries,
                db.chapterQueries,
                db.sectionQueries,
                get()
        )
    }
    single<SectionsRepository> {
        val db = get<Database>()
        SectionsDataStore(
                db.sectionQueries,
                db.ruleQueries,
                get()
        )
    }
    single<RulesRepository> {
        val db = get<Database>()
        RulesDataStore(
                db.ruleQueries,
                get()
        )
    }
    single<SearchRepository> {
        val db = get<Database>()
        SearchProvider(db.ruleQueries)
    }

    factory { Stemmer() }
    factory { SearchRules(get(), get()) }

    val cicerone = Cicerone.create()
    single { cicerone.router }
    single { cicerone.getNavigatorHolder() }

    viewModel { ContentsViewModel(get(), get()) }
    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get(), get()) }
    viewModel { (rulesToDisplay: RulesToDisplay) -> RulesViewModel(rulesToDisplay) }
    viewModel { (ruleId: Long, displaySectionButton: Boolean) ->
        RuleViewModel(ruleId, displaySectionButton, get(), get())
    }
    viewModel { SearchViewModel(get(), get(), get()) }
}