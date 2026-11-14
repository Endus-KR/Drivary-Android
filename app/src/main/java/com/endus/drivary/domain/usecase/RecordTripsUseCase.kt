package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.repository.DemoSettingsRepository
import com.endus.drivary.domain.repository.PlaceRepository
import com.endus.drivary.domain.repository.TripRepository
import com.endus.drivary.domain.repository.VehicleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/**
 * 기어 변화를 관찰해 운행을 기록한다. 앱이 떠 있는 동안 계속 실행된다.
 *
 * - 시작: P/N → D/R. 그 순간 계기판 값을 주행 전 거리로 저장
 * - 종료 후보: → P. 그 순간 계기판 값을 주행 후 거리로 저장
 * - 확정: P 후 확정 대기 시간 동안 재출발이 없으면 확정
 * - 병합: 확정 전에 재출발하면 같은 운행으로 이어 붙인다
 * - 버림: 주행거리가 0 이하인 운행
 * - 확정 시 도착지(시연 설정의 다음 도착지)를 붙이고 자동 분류한다
 *
 * 계기판 조회가 실패하면 그 기어 이벤트만 건너뛰고 [onError]로 알린 뒤 기록을 계속한다.
 */
class RecordTripsUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository,
    private val demoSettingsRepository: DemoSettingsRepository,
    private val placeRepository: PlaceRepository,
    private val autoClassifyTripUseCase: AutoClassifyTripUseCase,
    private val clock: Clock,
) {

    suspend operator fun invoke(onError: (Throwable) -> Unit = {}): Result<Unit> = runCatching {
        coroutineScope {
            val recorder = Recorder(this, onError)
            vehicleRepository.observeGear()
                .distinctUntilChanged()
                .collect { gear -> recorder.onGearChanged(gear) }
        }
    }

    /** 진행 중인 운행 상태를 관리한다 */
    private inner class Recorder(
        private val scope: CoroutineScope,
        private val onError: (Throwable) -> Unit,
    ) {

        private val mutex = Mutex()
        private var pending: PendingTrip? = null
        private var confirmJob: Job? = null

        suspend fun onGearChanged(gear: Gear) {
            // 시각은 기어 이벤트를 받은 순간 기준
            val eventAt = clock.instant()
            when {
                gear.isMoving -> onMove(eventAt)
                gear == Gear.PARK -> onPark(eventAt)
                else -> Unit // N, 알 수 없음: 상태를 바꾸지 않는다
            }
        }

        private suspend fun readOdometerOrNull(): Double? = try {
            vehicleRepository.getOdometerKm()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError(e)
            null
        }

        private suspend fun onMove(eventAt: Instant) {
            mutex.withLock {
                val current = pending
                when {
                    // 새 운행 시작
                    current == null -> {
                        val odometer = readOdometerOrNull() ?: return
                        pending = PendingTrip(startedAt = eventAt, odometerBeforeKm = odometer)
                    }
                    // 확정 전 재출발: 같은 운행으로 이어 붙인다
                    current.isParked -> {
                        confirmJob?.cancel()
                        confirmJob = null
                        pending = current.copy(endedAt = null, odometerAfterKm = null)
                    }
                    // 이미 주행 중 (D ↔ R 등)
                    else -> Unit
                }
            }
        }

        private suspend fun onPark(eventAt: Instant) {
            mutex.withLock {
                val current = pending ?: return
                if (current.isParked) return

                val odometer = readOdometerOrNull() ?: return
                val parked = current.copy(endedAt = eventAt, odometerAfterKm = odometer)
                pending = parked

                confirmJob = scope.launch {
                    delay(demoSettingsRepository.observeConfirmDelay().first())
                    mutex.withLock {
                        // 대기 중 재출발했다면 pending이 바뀌어 있다
                        if (pending !== parked) return@withLock
                        pending = null
                        confirmJob = null
                        parked.toTripOrNull()?.let { trip -> tripRepository.addTrip(withDestinationAndClassification(trip)) }
                    }
                }
            }
        }
    }

    /** 도착지를 붙이고 자동 분류한다. 실패해도 운행은 미처리로 남긴다 */
    private suspend fun withDestinationAndClassification(trip: Trip): Trip {
        val destinationId = demoSettingsRepository.observeNextDestinationId().first()
        val destination = placeRepository.getPlaces().firstOrNull { it.id == destinationId }
        val withDestination = trip.copy(destination = destination)
        val classification = autoClassifyTripUseCase(withDestination)
            .getOrDefault(TripClassification.Unclassified)
        return withDestination.copy(classification = classification)
    }

    private data class PendingTrip(
        val startedAt: Instant,
        val odometerBeforeKm: Double,
        val endedAt: Instant? = null,
        val odometerAfterKm: Double? = null,
    ) {
        val isParked: Boolean
            get() = endedAt != null

        /** 주행거리가 0 이하면 버린다 */
        fun toTripOrNull(): Trip? {
            val end = endedAt ?: return null
            val after = odometerAfterKm ?: return null
            if (after - odometerBeforeKm <= 0.0) return null
            return Trip(
                startedAt = startedAt,
                endedAt = end,
                odometerBeforeKm = odometerBeforeKm,
                odometerAfterKm = after,
            )
        }
    }
}
