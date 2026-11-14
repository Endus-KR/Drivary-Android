package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.PendingTripCard
import com.endus.drivary.domain.model.RedDayCalendar
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.repository.TripRepository
import com.endus.drivary.domain.repository.WorkPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject

/** 가장 오래된 미처리 운행 1건을 카드로 관찰한다. 없으면 null */
class ObservePendingTripCardUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val workPlaceRepository: WorkPlaceRepository,
    private val clock: Clock,
) {

    operator fun invoke(): Flow<PendingTripCard?> =
        tripRepository.observeTrips().map { trips ->
            val pending = trips.filter { it.classification == TripClassification.Unclassified }
            val oldest = pending.minByOrNull { it.startedAt } ?: return@map null
            PendingTripCard(
                trip = oldest,
                suggestedPurpose = oldest.destination?.let { workPlaceRepository.getLastPurpose(it.id) },
                isRedDay = RedDayCalendar.isRedDay(oldest.startedAt.atZone(clock.zone).toLocalDate()),
                pendingCount = pending.size,
            )
        }
}
