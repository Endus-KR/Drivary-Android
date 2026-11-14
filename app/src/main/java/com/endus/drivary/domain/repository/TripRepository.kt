package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Trip
import com.endus.drivary.domain.model.TripClassification
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    /** 저장된 운행 목록을 시작 시각 오래된 순으로 관찰한다 */
    fun observeTrips(): Flow<List<Trip>>

    /** 새 운행을 저장하고 id가 부여된 운행을 돌려준다 */
    suspend fun addTrip(trip: Trip): Trip

    /** 운행의 분류를 바꾸고 바뀐 운행을 돌려준다 */
    suspend fun updateClassification(tripId: Long, classification: TripClassification): Trip
}
