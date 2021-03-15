package me.alex.pet.apps.zhishi.di

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.common.CopyOpenHelperFactory
import me.alex.pet.apps.zhishi.data.rules.RulesDataStore
import me.alex.pet.apps.zhishi.data.search.SuggestionsProvider
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
        val callback = object : SupportSQLiteOpenHelper.Callback(Database.Schema.version) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // Do nothing
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                // Do nothing
            }
        }
        AndroidSqliteDriver(Database.Schema, androidContext(), "rules.db", factory, callback)
    }
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

    single<SuggestionsRepository> { SuggestionsProvider() }

    factory { Stemmer() }
    factory { SearchRules(get(), get()) }

    viewModel { ContentsViewModel(get()) }
    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get()) }
    viewModel { (ruleId: Long) -> RuleViewModel(ruleId, get()) }
    viewModel { SearchViewModel(get(), get()) }
}