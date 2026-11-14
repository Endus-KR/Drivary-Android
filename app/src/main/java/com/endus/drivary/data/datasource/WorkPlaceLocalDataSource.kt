package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.TripPurpose
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 업무 장소별 마지막 목적 (메모리).
 * 시연 첫 장면부터 자동 분류를 보여 주도록 본사·한빛물류센터를 미리 등록해 둔다.
 */
@Singleton
class WorkPlaceLocalDataSource @Inject constructor() {

    private val lastPurposes = ConcurrentHashMap(
        mapOf(
            PLACE_ID_HEADQUARTERS to TripPurpose.COMMUTE,
            PLACE_ID_LOGISTICS_CENTER to TripPurpose.DELIVERY,
        ),
    )

    fun getLastPurpose(placeId: Long): TripPurpose? = lastPurposes[placeId]

    fun setLastPurpose(placeId: Long, purpose: TripPurpose) {
        lastPurposes[placeId] = purpose
    }

    private companion object {
        // PlaceLocalDataSource의 고정 장소 id
        const val PLACE_ID_HEADQUARTERS = 1L
        const val PLACE_ID_LOGISTICS_CENTER = 3L
    }
}
