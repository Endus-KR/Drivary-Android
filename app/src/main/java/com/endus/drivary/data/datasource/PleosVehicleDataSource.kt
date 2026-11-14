package com.endus.drivary.data.datasource

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.endus.drivary.data.mapper.toDomain
import com.endus.drivary.domain.model.Gear
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Pleos Vehicle SDK 2.3.2로 기어·계기판을 읽는다. SDK 클래스는 이 파일 밖으로 내보내지 않는다.
 *
 * 기어 API(getGear)는 공식 레퍼런스에 없지만 SDK에 들어 있는 API다.
 */
@Singleton
class PleosVehicleDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : VehicleDataSource {

    private val initLock = Mutex()
    private var vehicle: Vehicle? = null

    /**
     * 이 기기에 Pleos Vehicle 서비스가 있는지 확인한다.
     * 없는 기기에서 SDK를 초기화하면 서비스 바인딩을 계속 재시도하므로 미리 걸러낸다.
     */
    fun isServiceAvailable(): Boolean {
        val packageManager = context.packageManager
        val installed = runCatching {
            packageManager.getPackageInfo(VEHICLE_SERVICE_PACKAGE, 0)
        }.isSuccess
        if (installed) return true
        return runCatching {
            packageManager.queryIntentServices(Intent(VEHICLE_SERVICE_ACTION), PackageManager.MATCH_ALL)
                .isNotEmpty()
        }.getOrDefault(false)
    }

    @Suppress("DEPRECATION")
    override fun observeGear(): Flow<Gear> = callbackFlow {
        val gear = obtainVehicle().getGear()
        val listener = object : GearSelectionListener {
            override fun onSelected(value: VehicleGear?) {
                trySend(value.toDomain())
            }

            override fun onFailed(e: Exception) {
                close(e)
            }
        }
        gear.registerGearSelection(listener)
        // 리스너는 변화만 알려 주므로 현재 기어를 한 번 조회해 먼저 내보낸다
        gear.getCurrentGearSelectionState(
            { value -> trySend(value.toDomain()) },
            { e -> Timber.e(e) },
        )
        awaitClose {
            runCatching { gear.unregisterGearSelection(listener) }
        }
    }.retryWhen { cause, attempt ->
        // initialize()는 서비스 바인딩이 비동기라 첫 등록이 실패할 수 있다
        Timber.e(cause)
        if (attempt < MAX_RETRY) {
            delay(RETRY_DELAY_MS * (attempt + 1))
            true
        } else {
            false
        }
    }

    override suspend fun getOdometerKm(): Double {
        val odometer = obtainVehicle().getOdometer()
        val km: Float? = withTimeout(TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                odometer.getCurrentDistanceDriven(
                    { value: Float? -> if (cont.isActive) cont.resume(value) },
                    { e: Exception -> if (cont.isActive) cont.resumeWithException(e) },
                )
            }
        }
        return checkNotNull(km) { "계기판 값을 받지 못했다" }.toDouble()
    }

    private suspend fun obtainVehicle(): Vehicle = initLock.withLock {
        vehicle ?: Vehicle(context).also {
            it.initialize()
            vehicle = it
        }
    }

    private companion object {
        const val VEHICLE_SERVICE_PACKAGE = "ai.pleos.playground.service.vehicle"
        const val VEHICLE_SERVICE_ACTION = "ai.pleos.playground.vehicle.VEHICLE_START"
        const val MAX_RETRY = 4L
        const val RETRY_DELAY_MS = 800L
        const val TIMEOUT_MS = 5_000L
    }
}
