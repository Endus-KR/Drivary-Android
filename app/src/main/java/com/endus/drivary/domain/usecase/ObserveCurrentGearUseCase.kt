package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentGearUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
) {
    operator fun invoke(): Flow<Gear> = vehicleRepository.observeGear()
}
