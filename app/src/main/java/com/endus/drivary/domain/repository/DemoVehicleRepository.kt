package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Gear
import kotlinx.coroutines.flow.Flow

/** 시연용 차량 조작. 디버그 설정 화면에서 사용한다 */
interface DemoVehicleRepository {

    /** 계기판 값에 더하는 시연용 추가 주행거리(km)를 관찰한다 */
    fun observeExtraDistanceKm(): Flow<Double>

    /** Fake 차량의 기어를 바꾼다. 시연 모드에서만 효과가 있다 */
    suspend fun setGear(gear: Gear)

    /**
     * 시연용 추가 주행거리를 늘린다. 시연 모드와 Pleos 모드 모두 계기판 값에 더해진다
     * (에뮬레이터 Virtual Console은 계기판 값을 바꿀 수 없어서 쓴다).
     */
    suspend fun addDistance(km: Double)
}
