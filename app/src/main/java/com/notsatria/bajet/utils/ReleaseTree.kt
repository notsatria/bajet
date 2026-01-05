package com.notsatria.bajet.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance()

        crashlytics.log(message)

        if (t != null) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                crashlytics.recordException(t)
            }
        }
    }
}