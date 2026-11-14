package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.TripLocalDataSource
import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripLocalDataSource: TripLocalDataSource,
) : TripRepository {

    override fun observeTrips(): Flow<List<Trip>> = tripLocalDataSource.observeTrips()

    override suspend fun addTrip(trip: Trip): Trip = tripLocalDataSource.insert(trip)
}
