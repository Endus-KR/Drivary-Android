package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Gear
import kotlinx.coroutines.flow.Flow

/** 시연 모드(Fake 차량) 조작. 디버그 설정 화면에서 사용한다 */
interface DemoVehicleRepository {

    /** Fake 차량의 계기판 값(km)을 관찰한다 */
    fun observeOdometerKm(): Flow<Double>

    /** Fake 차량의 기어를 바꾼다 */
    suspend fun setGear(gear: Gear)

    /** Fake 차량의 계기판 값을 늘린다 */
    suspend fun addDistance(km: Double)
}
