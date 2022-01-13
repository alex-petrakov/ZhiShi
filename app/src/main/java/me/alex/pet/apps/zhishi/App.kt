package me.alex.pet.apps.zhishi

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import me.alex.pet.apps.zhishi.domain.settings.ThemeManager
import timber.log.Timber
import javax.inject.Inject

@Suppress("unused") // Used in AndroidManifest.xml
@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        }
    }
}