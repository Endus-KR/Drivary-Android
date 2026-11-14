package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.Gear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Pleos 차량·에뮬레이터가 없을 때 쓰는 시연용 차량 */
@Singleton
class FakeVehicleDataSource @Inject constructor() : VehicleDataSource {

    private val gear = MutableStateFlow(Gear.PARK)
    private val odometerKm = MutableStateFlow(INITIAL_ODOMETER_KM)

    val odometer: StateFlow<Double> = odometerKm.asStateFlow()

    override fun observeGear(): Flow<Gear> = gear.asStateFlow()

    override suspend fun getOdometerKm(): Double = odometerKm.value

    fun setGear(newGear: Gear) {
        gear.value = newGear
    }

    fun addDistance(km: Double) {
        odometerKm.update { it + km }
    }

    private companion object {
        const val INITIAL_ODOMETER_KM = 12_345.6
    }
}
