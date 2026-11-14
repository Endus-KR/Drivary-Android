package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.DemoDistanceDataSource
import com.endus.drivary.data.datasource.VehicleDataSource
import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.model.VehicleMode
import com.endus.drivary.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDataSource: VehicleDataSource,
    private val demoDistanceDataSource: DemoDistanceDataSource,
) : VehicleRepository {

    override val mode: VehicleMode
        get() = vehicleDataSource.mode

    override fun observeGear(): Flow<Gear> = vehicleDataSource.observeGear()

    override suspend fun getOdometerKm(): Double =
        vehicleDataSource.getOdometerKm() + demoDistanceDataSource.current.value
}
