package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.Gear
import kotlinx.coroutines.flow.Flow

/** 차량 데이터 원천. Pleos SDK 또는 Fake 구현으로 교체한다 */
interface VehicleDataSource {

    fun observeGear(): Flow<Gear>

    suspend fun getOdometerKm(): Double
}
