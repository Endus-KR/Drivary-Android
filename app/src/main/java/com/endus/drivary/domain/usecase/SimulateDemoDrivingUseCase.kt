package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoSettingsRepository
import com.endus.drivary.domain.repository.DemoVehicleRepository
import com.endus.drivary.domain.repository.VehicleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

/**
 * 시연용: 주행 중(D/R)이고 자동 증가가 켜져 있으면 계기판 값을 1초마다 늘린다.
 * 앱이 떠 있는 동안 계속 실행된다. 기어 P/N이 되면 바로 멈춘다.
 */
class SimulateDemoDrivingUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val demoSettingsRepository: DemoSettingsRepository,
    private val demoVehicleRepository: DemoVehicleRepository,
) {

    suspend operator fun invoke(): Result<Unit> = runCatching {
        combine(
            vehicleRepository.observeGear(),
            demoSettingsRepository.observeAutoDistanceEnabled(),
        ) { gear, enabled -> gear.isMoving && enabled }
            .distinctUntilChanged()
            .collectLatest { driving ->
                while (driving) {
                    delay(TICK_MS)
                    demoVehicleRepository.addDistance(KM_PER_TICK)
                }
            }
    }

    private companion object {
        const val TICK_MS = 1_000L

        /** 1초에 0.5km. 10초 주행이면 5km */
        const val KM_PER_TICK = 0.5
    }
}
