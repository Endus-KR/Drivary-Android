package com.endus.drivary.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/** 시연 파라미터 */
interface DemoSettingsRepository {

    /** P 이후 운행을 확정하기까지 기다리는 시간 */
    fun observeConfirmDelay(): Flow<Duration>

    suspend fun setConfirmDelay(delay: Duration)
}
