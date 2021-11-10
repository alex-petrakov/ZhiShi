package me.alex.pet.apps.zhishi.di

import android.content.Context
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.alex.pet.apps.zhishi.ChapterQueries
import me.alex.pet.apps.zhishi.PartQueries
import me.alex.pet.apps.zhishi.RuleQueries
import me.alex.pet.apps.zhishi.SectionQueries
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.common.CopyOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonDiModule {

    private const val DB_FILENAME = "rules.db"

    private const val DB_ASSET_PATH = "db/rules.db"

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): Database {
        val driver = AndroidSqliteDriver(
            Database.Schema,
            context,
            DB_FILENAME,
            CopyOpenHelperFactory(DB_ASSET_PATH)
        )
        return Database(driver)
    }

    @Provides
    @Singleton
    fun partQueries(db: Database): PartQueries = db.partQueries

    @Provides
    @Singleton
    fun chapterQueries(db: Database): ChapterQueries = db.chapterQueries

    @Provides
    @Singleton
    fun sectionQueries(db: Database): SectionQueries = db.sectionQueries

    @Provides
    @Singleton
    fun ruleQueries(db: Database): RuleQueries = db.ruleQueries

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun cicerone(): Cicerone<Router> = Cicerone.create()

    @Provides
    @Singleton
    fun router(cicerone: Cicerone<Router>): Router = cicerone.router

    @Provides
    @Singleton
    fun navigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder =
        cicerone.getNavigatorHolder()
}