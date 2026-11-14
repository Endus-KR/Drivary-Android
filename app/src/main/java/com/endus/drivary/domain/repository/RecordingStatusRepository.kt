package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.RecordingStatus
import kotlinx.coroutines.flow.Flow

interface RecordingStatusRepository {

    fun observeStatus(): Flow<RecordingStatus>

    suspend fun setStatus(status: RecordingStatus)
}
