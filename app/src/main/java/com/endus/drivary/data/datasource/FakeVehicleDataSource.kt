package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.model.VehicleMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Pleos 차량·에뮬레이터가 없을 때 쓰는 시연용 차량. 주행거리는 [DemoDistanceDataSource]로 늘린다 */
@Singleton
class FakeVehicleDataSource @Inject constructor() : VehicleDataSource {

    private val gear = MutableStateFlow(Gear.PARK)

    override val mode: VehicleMode = VehicleMode.DEMO

    override fun observeGear(): Flow<Gear> = gear.asStateFlow()

    override suspend fun getOdometerKm(): Double = INITIAL_ODOMETER_KM

    fun setGear(newGear: Gear) {
        gear.value = newGear
    }

    private companion object {
        const val INITIAL_ODOMETER_KM = 12_345.6
    }
}
