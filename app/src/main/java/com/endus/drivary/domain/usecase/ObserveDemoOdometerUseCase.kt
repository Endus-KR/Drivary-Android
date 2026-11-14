package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoVehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDemoOdometerUseCase @Inject constructor(
    private val demoVehicleRepository: DemoVehicleRepository,
) {
    operator fun invoke(): Flow<Double> = demoVehicleRepository.observeOdometerKm()
}
