package com.endus.drivary.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** 시연 파라미터 저장소 (메모리) */
@Singleton
class DemoSettingsLocalDataSource @Inject constructor() {

    private val confirmDelay = MutableStateFlow(DEFAULT_CONFIRM_DELAY)
    private val nextDestinationId = MutableStateFlow<Long?>(DEFAULT_NEXT_DESTINATION_ID)
    private val treatTodayAsWeekday = MutableStateFlow(false)

    fun observeConfirmDelay(): Flow<Duration> = confirmDelay.asStateFlow()

    fun setConfirmDelay(delay: Duration) {
        confirmDelay.value = delay
    }

    fun observeNextDestinationId(): Flow<Long?> = nextDestinationId.asStateFlow()

    fun setNextDestinationId(placeId: Long?) {
        nextDestinationId.value = placeId
    }

    fun observeTreatTodayAsWeekday(): Flow<Boolean> = treatTodayAsWeekday.asStateFlow()

    fun setTreatTodayAsWeekday(enabled: Boolean) {
        treatTodayAsWeekday.value = enabled
    }

    private companion object {
        val DEFAULT_CONFIRM_DELAY = 10.seconds

        /** 본사 (PlaceLocalDataSource의 고정 장소 id) */
        const val DEFAULT_NEXT_DESTINATION_ID = 1L
    }
}
