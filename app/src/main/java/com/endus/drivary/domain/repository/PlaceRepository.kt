package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Place

interface PlaceRepository {

    /** 시연용 고정 장소 목록 */
    suspend fun getPlaces(): List<Place>
}
