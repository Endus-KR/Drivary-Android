package com.endus.drivary.domain.model

import java.time.Instant

/** 지금 운행 기록 상태. 홈 화면에 보여 준다 */
sealed interface RecordingStatus {

    /** 주차 중, 진행 중인 운행 없음 */
    data object Idle : RecordingStatus

    /** 주행 중 (D/R) */
    data class Driving(
        val startedAt: Instant,
        val odometerBeforeKm: Double,
    ) : RecordingStatus

    /** P 이후 확정 대기 중. 이 시각까지 재출발이 없으면 확정한다 */
    data class ConfirmPending(
        val startedAt: Instant,
        val odometerBeforeKm: Double,
        val odometerAfterKm: Double,
        val confirmAt: Instant,
    ) : RecordingStatus
}
