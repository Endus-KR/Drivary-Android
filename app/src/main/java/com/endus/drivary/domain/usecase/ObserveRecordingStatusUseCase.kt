package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.RecordingStatus
import com.endus.drivary.domain.repository.RecordingStatusRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecordingStatusUseCase @Inject constructor(
    private val recordingStatusRepository: RecordingStatusRepository,
) {
    operator fun invoke(): Flow<RecordingStatus> = recordingStatusRepository.observeStatus()
}
