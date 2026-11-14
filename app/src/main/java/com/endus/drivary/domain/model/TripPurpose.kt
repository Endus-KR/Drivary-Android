package com.endus.drivary.domain.model

/** 업무 운행의 목적 (고정 목록). 이름은 운행기록부 비고에 그대로 쓴다 */
enum class TripPurpose(
    val displayName: String,
    val usage: TripUsage,
) {
    COMMUTE("출퇴근", TripUsage.COMMUTE),
    SALES("영업", TripUsage.GENERAL_BUSINESS),
    DELIVERY("납품", TripUsage.GENERAL_BUSINESS),
    PURCHASE("구매", TripUsage.GENERAL_BUSINESS),
    MEETING("회의", TripUsage.GENERAL_BUSINESS),
    OTHER("기타", TripUsage.GENERAL_BUSINESS),
}
