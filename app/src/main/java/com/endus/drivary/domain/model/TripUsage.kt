package com.endus.drivary.domain.model

/** 국세청 운행기록부의 업무용 사용거리 칸 */
enum class TripUsage {
    /** 출·퇴근용 */
    COMMUTE,

    /** 일반 업무용 */
    GENERAL_BUSINESS,
}
