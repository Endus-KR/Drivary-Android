package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoVehicleRepository
import javax.inject.Inject

class AddDemoDistanceUseCase @Inject constructor(
    private val demoVehicleRepository: DemoVehicleRepository,
) {
    suspend operator fun invoke(km: Double): Result<Unit> = runCatching {
        require(km > 0.0) { "늘릴 거리는 0보다 커야 한다: $km" }
        demoVehicleRepository.addDistance(km)
    }
}
