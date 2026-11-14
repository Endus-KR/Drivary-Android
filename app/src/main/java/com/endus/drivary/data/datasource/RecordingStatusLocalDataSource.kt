package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.RecordingStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** 지금 운행 기록 상태 (메모리, 앱 실행 동안만 유지) */
@Singleton
class RecordingStatusLocalDataSource @Inject constructor() {

    private val status = MutableStateFlow<RecordingStatus>(RecordingStatus.Idle)

    fun observeStatus(): Flow<RecordingStatus> = status.asStateFlow()

    fun setStatus(newStatus: RecordingStatus) {
        status.value = newStatus
    }
}
