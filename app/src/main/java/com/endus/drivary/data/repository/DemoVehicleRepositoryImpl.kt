package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.DemoDistanceDataSource
import com.endus.drivary.data.datasource.FakeVehicleDataSource
import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.repository.DemoVehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DemoVehicleRepositoryImpl @Inject constructor(
    private val fakeVehicleDataSource: FakeVehicleDataSource,
    private val demoDistanceDataSource: DemoDistanceDataSource,
) : DemoVehicleRepository {

    override fun observeExtraDistanceKm(): Flow<Double> = demoDistanceDataSource.observe()

    override suspend fun setGear(gear: Gear) = fakeVehicleDataSource.setGear(gear)

    override suspend fun addDistance(km: Double) = demoDistanceDataSource.add(km)
}
