package com.endus.drivary

import android.app.Application
import com.endus.drivary.di.ApplicationScope
import com.endus.drivary.domain.usecase.RecordTripsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class DrivaryApplication : Application() {

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var recordTripsUseCase: RecordTripsUseCase

    override fun onCreate() {
        super.onCreate()
        // 디버그 빌드에서만 로그를 남긴다
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startTripRecording()
    }

    /** 화면 이동과 무관하게 앱이 떠 있는 동안 운행을 기록한다 */
    private fun startTripRecording() {
        applicationScope.launch {
            recordTripsUseCase()
                .onFailure { throwable -> Timber.e(throwable) }
        }
    }
}
