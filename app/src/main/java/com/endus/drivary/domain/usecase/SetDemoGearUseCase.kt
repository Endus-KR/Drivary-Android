package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.repository.DemoVehicleRepository
import javax.inject.Inject

class SetDemoGearUseCase @Inject constructor(
    private val demoVehicleRepository: DemoVehicleRepository,
) {
    suspend operator fun invoke(gear: Gear): Result<Unit> = runCatching {
        demoVehicleRepository.setGear(gear)
    }
}
