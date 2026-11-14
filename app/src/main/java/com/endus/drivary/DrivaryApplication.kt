package com.endus.drivary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DrivaryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 디버그 빌드에서만 로그를 남긴다
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
