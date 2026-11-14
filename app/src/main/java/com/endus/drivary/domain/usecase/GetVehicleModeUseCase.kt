package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.VehicleMode
import com.endus.drivary.domain.repository.VehicleRepository
import javax.inject.Inject

class GetVehicleModeUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
) {
    operator fun invoke(): Result<VehicleMode> = runCatching { vehicleRepository.mode }
}
