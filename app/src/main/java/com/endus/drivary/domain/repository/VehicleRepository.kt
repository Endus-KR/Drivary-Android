package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Gear
import com.endus.drivary.domain.model.VehicleMode
import kotlinx.coroutines.flow.Flow

/** 차량 상태 조회 (조회 전용) */
interface VehicleRepository {

    /** 차량 데이터를 어디서 받는지 */
    val mode: VehicleMode

    /** 기어 상태를 관찰한다. 구독 시 현재 기어를 먼저 내보낸다 */
    fun observeGear(): Flow<Gear>

    /** 현재 계기판 누적 주행거리(km)를 조회한다. 시연용 추가 주행거리가 더해진다 */
    suspend fun getOdometerKm(): Double
}
