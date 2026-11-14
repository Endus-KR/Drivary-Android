package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.RecordingStatusLocalDataSource
import com.endus.drivary.domain.model.RecordingStatus
import com.endus.drivary.domain.repository.RecordingStatusRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecordingStatusRepositoryImpl @Inject constructor(
    private val recordingStatusLocalDataSource: RecordingStatusLocalDataSource,
) : RecordingStatusRepository {

    override fun observeStatus(): Flow<RecordingStatus> = recordingStatusLocalDataSource.observeStatus()

    override suspend fun setStatus(status: RecordingStatus) = recordingStatusLocalDataSource.setStatus(status)
}
