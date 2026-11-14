package com.endus.drivary.domain.repository

import com.endus.drivary.domain.model.Driver

interface DriverRepository {

    /** 운행기록부 사용자. 지금은 더미 값, 이후 초기 설정에서 입력받는다 */
    suspend fun getDriver(): Driver
}
