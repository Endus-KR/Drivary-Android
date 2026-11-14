package com.endus.drivary.domain.model

import java.time.Instant

/**
 * 확정된 운행 1건.
 *
 * 계기판 값(km)은 원본 소수점 그대로 저장한다. 반올림은 서식 출력 때만 한다.
 * 시각은 기어 이벤트를 받은 순간 기준이다.
 */
data class Trip(
    val id: Long = NEW_ID,
    val startedAt: Instant,
    val endedAt: Instant,
    val odometerBeforeKm: Double,
    val odometerAfterKm: Double,
) {
    /** 원본 계기판 값 기준 주행거리(km) */
    val distanceKm: Double
        get() = odometerAfterKm - odometerBeforeKm

    companion object {
        /** 저장 전 운행의 id */
        const val NEW_ID = 0L
    }
}
