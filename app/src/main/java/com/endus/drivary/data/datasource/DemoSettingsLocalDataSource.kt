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

    fun observeConfirmDelay(): Flow<Duration> = confirmDelay.asStateFlow()

    fun setConfirmDelay(delay: Duration) {
        confirmDelay.value = delay
    }

    private companion object {
        val DEFAULT_CONFIRM_DELAY = 10.seconds
    }
}
