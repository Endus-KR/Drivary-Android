package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/** 운행 저장소 (메모리). 이후 Room으로 교체한다 */
@Singleton
class TripLocalDataSource @Inject constructor() {

    private val trips = MutableStateFlow<List<Trip>>(emptyList())
    private val nextId = AtomicLong(1)

    fun observeTrips(): Flow<List<Trip>> = trips.asStateFlow()

    fun insert(trip: Trip): Trip {
        val saved = trip.copy(id = nextId.getAndIncrement())
        trips.update { current -> (current + saved).sortedBy { it.startedAt } }
        return saved
    }
}
