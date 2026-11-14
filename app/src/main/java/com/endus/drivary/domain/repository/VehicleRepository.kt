package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Gear
import kotlinx.coroutines.flow.Flow

/** 차량 상태 조회 (조회 전용) */
interface VehicleRepository {

    /** 기어 상태를 관찰한다. 구독 시 현재 기어를 먼저 내보낸다 */
    fun observeGear(): Flow<Gear>

    /** 현재 계기판 누적 주행거리(km)를 조회한다 */
    suspend fun getOdometerKm(): Double
}
