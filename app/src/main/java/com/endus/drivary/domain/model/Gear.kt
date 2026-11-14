package com.endus.drivary.domain.model

/** 차량 기어 상태 */
enum class Gear {
    PARK,
    REVERSE,
    NEUTRAL,
    DRIVE,
    UNKNOWN;

    /** 운행 중인 기어인지 (D/R) */
    val isMoving: Boolean
        get() = this == DRIVE || this == REVERSE
}
