package me.alex.pet.apps.zhishi.di

import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.common.CopyOpenHelperFactory
import me.alex.pet.apps.zhishi.data.contents.ContentsDataStore
import me.alex.pet.apps.zhishi.data.rules.RulesDataStore
import me.alex.pet.apps.zhishi.data.search.SuggestionsProvider
import me.alex.pet.apps.zhishi.domain.contents.ContentsRepository
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.domain.search.SearchRules
import me.alex.pet.apps.zhishi.domain.search.SuggestionsRepository
import me.alex.pet.apps.zhishi.domain.search.stemming.Stemmer
import me.alex.pet.apps.zhishi.presentation.contents.ContentsViewModel
import me.alex.pet.apps.zhishi.presentation.rule.RuleViewModel
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
    single<RulesRepository> {
        val db = get<Database>()
        RulesDataStore(
                db.sectionQueries,
                db.ruleQueries,
                get()
        )
    }

    single<SuggestionsRepository> { SuggestionsProvider() }

    factory { Stemmer() }
    factory { SearchRules(get(), get()) }

    viewModel { ContentsViewModel(get()) }
    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get()) }
    viewModel { (ruleId: Long) -> RuleViewModel(ruleId, get()) }
    viewModel { SearchViewModel(get(), get()) }
}