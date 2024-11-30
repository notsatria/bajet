package com.notsatria.bajet

import android.app.Application
import com.notsatria.bajet.BuildConfig.*
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}