package com.endus.drivary.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** 계기판 값에 더하는 시연용 추가 주행거리(km). 디버그 설정에서만 늘린다 */
@Singleton
class DemoDistanceDataSource @Inject constructor() {

    private val extraKm = MutableStateFlow(0.0)

    val current: StateFlow<Double> = extraKm.asStateFlow()

    fun observe(): Flow<Double> = current

    fun add(km: Double) {
        extraKm.update { it + km }
    }
}
