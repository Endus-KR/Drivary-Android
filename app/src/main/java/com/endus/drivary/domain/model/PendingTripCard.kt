package com.endus.drivary.domain.model

/**
 * 미처리 카드로 물을 운행 1건.
 *
 * @property suggestedPurpose 도착지의 마지막 업무 목적. 있으면 "~하러 방문하셨나요?"로 묻는다
 * @property isRedDay 빨간날 운행 여부
 * @property pendingCount 남은 미처리 운행 수 (이 운행 포함)
 */
data class PendingTripCard(
    val trip: Trip,
    val suggestedPurpose: TripPurpose?,
    val isRedDay: Boolean,
    val pendingCount: Int,
)
