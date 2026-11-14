package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.PlaceLocalDataSource
import com.endus.drivary.domain.model.Place
import com.endus.drivary.domain.repository.PlaceRepository
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeLocalDataSource: PlaceLocalDataSource,
) : PlaceRepository {

    override suspend fun getPlaces(): List<Place> = placeLocalDataSource.getPlaces()
}
