package com.endus.drivary.data.datasource

import com.endus.drivary.domain.model.Driver
import javax.inject.Inject
import javax.inject.Singleton

/** 운행기록부 사용자. 지금은 더미 값, 이후 초기 설정에서 입력받는다 */
@Singleton
class DriverLocalDataSource @Inject constructor() {

    fun getDriver(): Driver = DUMMY_DRIVER

    private companion object {
        val DUMMY_DRIVER = Driver(department = "영업팀", name = "홍길동")
    }
}
