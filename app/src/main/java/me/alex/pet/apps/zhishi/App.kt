package me.alex.pet.apps.zhishi

import android.app.Application
import me.alex.pet.apps.zhishi.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused") // Used in AndroidManifest.xml
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(commonModule, contentsModule, sectionsModule, rulesModule, searchModule)
        }
    }
}