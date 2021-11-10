package me.alex.pet.apps.zhishi

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@Suppress("unused") // Used in AndroidManifest.xml
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        }
    }
}