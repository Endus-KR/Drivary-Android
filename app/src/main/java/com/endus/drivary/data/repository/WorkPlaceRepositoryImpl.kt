package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.WorkPlaceLocalDataSource
import com.endus.drivary.domain.model.TripPurpose
import com.endus.drivary.domain.repository.WorkPlaceRepository
import javax.inject.Inject

class WorkPlaceRepositoryImpl @Inject constructor(
    private val workPlaceLocalDataSource: WorkPlaceLocalDataSource,
) : WorkPlaceRepository {

    override suspend fun getLastPurpose(placeId: Long): TripPurpose? =
        workPlaceLocalDataSource.getLastPurpose(placeId)

    override suspend fun setLastPurpose(placeId: Long, purpose: TripPurpose) =
        workPlaceLocalDataSource.setLastPurpose(placeId, purpose)
}
