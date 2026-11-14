package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.repository.TripRepository
import com.endus.drivary.domain.repository.WorkPlaceRepository
import javax.inject.Inject

/**
 * 운행을 분류한다.
 * 업무로 분류하면 도착지를 업무 장소로 등록하고 마지막 목적을 바꾼다 (다음 평일 방문은 자동 분류).
 */
class ClassifyTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val workPlaceRepository: WorkPlaceRepository,
) {

    suspend operator fun invoke(tripId: Long, classification: TripClassification): Result<Trip> = runCatching {
        require(classification != TripClassification.Unclassified) { "미처리로 되돌릴 수 없다" }
        val trip = tripRepository.updateClassification(tripId, classification)
        val destination = trip.destination
        if (classification is TripClassification.Business && destination != null) {
            workPlaceRepository.setLastPurpose(destination.id, classification.purpose)
        }
        trip
    }
}
