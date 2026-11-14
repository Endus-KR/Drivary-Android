package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.repository.WorkPlaceRepository
import javax.inject.Inject

/**
 * 새 운행의 자동 분류.
 * 평일 + 등록된 업무 장소 도착이면 업무 + 그 장소의 마지막 목적. 그 외(처음 가는 곳, 빨간날)는 미처리.
 */
class AutoClassifyTripUseCase @Inject constructor(
    private val workPlaceRepository: WorkPlaceRepository,
    private val checkRedDayUseCase: CheckRedDayUseCase,
) {

    suspend operator fun invoke(trip: Trip): Result<TripClassification> = runCatching {
        val destination = trip.destination ?: return@runCatching TripClassification.Unclassified
        if (checkRedDayUseCase(trip.startedAt)) return@runCatching TripClassification.Unclassified
        val lastPurpose = workPlaceRepository.getLastPurpose(destination.id)
            ?: return@runCatching TripClassification.Unclassified
        TripClassification.Business(lastPurpose)
    }
}
