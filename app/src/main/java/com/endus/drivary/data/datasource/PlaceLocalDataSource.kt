package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.Place
import javax.inject.Inject
import javax.inject.Singleton

/** 시연용 고정 장소 목록 (가상의 자동차 부품 유통 회사 기준) */
@Singleton
class PlaceLocalDataSource @Inject constructor() {

    fun getPlaces(): List<Place> = PLACES

    private companion object {
        val PLACES = listOf(
            Place(id = 1, name = "본사"),
            Place(id = 2, name = "집"),
            Place(id = 3, name = "한빛물류센터"),
            Place(id = 4, name = "대성부품 대리점"),
            Place(id = 5, name = "미래오토 정비소"),
            Place(id = 6, name = "동네 마트"),
        )
    }
}
