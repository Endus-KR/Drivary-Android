package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) {
    operator fun invoke(): Flow<List<Trip>> = tripRepository.observeTrips()
}
