package com.endus.drivary.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/** 시연 파라미터 */
interface DemoSettingsRepository {

    /** P 이후 운행을 확정하기까지 기다리는 시간 */
    fun observeConfirmDelay(): Flow<Duration>

    suspend fun setConfirmDelay(delay: Duration)

    /** 다음에 확정되는 운행의 도착지 id (장소 학습 대신 시연에서 고른다) */
    fun observeNextDestinationId(): Flow<Long?>

    suspend fun setNextDestinationId(placeId: Long?)

    /** 시연이 주말에 있어도 평일 자동 분류를 보여 주기 위해 오늘을 평일로 본다 */
    fun observeTreatTodayAsWeekday(): Flow<Boolean>

    suspend fun setTreatTodayAsWeekday(enabled: Boolean)

    /** 시연에서 주행 중(D/R)일 때 계기판 값을 저절로 늘린다 (에뮬레이터는 계기판이 움직이지 않는다) */
    fun observeAutoDistanceEnabled(): Flow<Boolean>

    suspend fun setAutoDistanceEnabled(enabled: Boolean)
}
