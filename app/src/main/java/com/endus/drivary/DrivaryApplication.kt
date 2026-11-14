package com.endus.drivary

import android.app.Application
import com.endus.drivary.di.ApplicationScope
import com.endus.drivary.domain.usecase.RecordTripsUseCase
import com.endus.drivary.domain.usecase.SimulateDemoDrivingUseCase
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

    @Inject
    lateinit var simulateDemoDrivingUseCase: SimulateDemoDrivingUseCase

    override fun onCreate() {
        // Hilt가 super.onCreate()에서 의존성을 주입하며 로그를 남기므로 그보다 먼저 심는다
        // 디버그 빌드에서만 로그를 남긴다
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        super.onCreate()
        startTripRecording()
        startDemoDriving()
    }

    /** 시연용: 주행 중 계기판 자동 증가 (디버그 설정에서 끌 수 있다) */
    private fun startDemoDriving() {
        applicationScope.launch {
            simulateDemoDrivingUseCase()
                .onFailure { throwable -> Timber.e(throwable) }
        }
    }

    /** 화면 이동과 무관하게 앱이 떠 있는 동안 운행을 기록한다 */
    private fun startTripRecording() {
        applicationScope.launch {
            recordTripsUseCase(onError = { throwable -> Timber.e(throwable) })
                .onFailure { throwable -> Timber.e(throwable) }
        }
    }
}
