package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.TripPurpose

/** 등록된 업무 장소와 그 장소의 마지막 업무 목적 */
interface WorkPlaceRepository {

    /** 업무 장소로 등록돼 있으면 마지막 목적, 아니면 null */
    suspend fun getLastPurpose(placeId: Long): TripPurpose?

    /** 장소를 업무 장소로 등록하고 마지막 목적을 바꾼다 */
    suspend fun setLastPurpose(placeId: Long, purpose: TripPurpose)
}
