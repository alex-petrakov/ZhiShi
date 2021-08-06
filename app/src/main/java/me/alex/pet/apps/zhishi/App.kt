package me.alex.pet.apps.zhishi

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import me.alex.pet.apps.zhishi.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("unused") // Used in AndroidManifest.xml
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        }

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(level = Level.DEBUG)
            }
            androidContext(this@App)
            modules(commonModule, contentsModule, sectionsModule, rulesModule, searchModule)
        }
    }
}