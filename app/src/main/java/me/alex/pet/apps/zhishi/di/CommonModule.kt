package me.alex.pet.apps.zhishi.di

import android.content.Context
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.common.CopyOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {

    single { createDatabase(androidContext()) }

    single<Moshi> { Moshi.Builder().build() }

    single { Cicerone.create() }
    single { get<Cicerone<Router>>().router }
    single { get<Cicerone<Router>>().getNavigatorHolder() }
}

private fun createDatabase(context: Context): Database {
    val driver = AndroidSqliteDriver(
            Database.Schema,
            context,
            "rules.db",
            CopyOpenHelperFactory("db/rules.db")
    )
    return Database(driver)
}