package com.endus.drivary.domain.model

/** 차량 데이터를 어디서 받는지 */
enum class VehicleMode {
    /** Pleos 차량·에뮬레이터 */
    PLEOS,

    /** Pleos 서비스가 없는 기기의 시연 모드(Fake 차량) */
    DEMO,
}
