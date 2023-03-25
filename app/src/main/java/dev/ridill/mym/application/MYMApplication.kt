package dev.ridill.mym.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.ridill.mym.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class MYMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}