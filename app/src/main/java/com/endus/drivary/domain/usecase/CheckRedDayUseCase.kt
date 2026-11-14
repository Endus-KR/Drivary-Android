package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.model.RedDayCalendar
import com.endus.drivary.domain.repository.DemoSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * 운행 시각이 빨간날인지 판단한다.
 * 시연 설정 "오늘을 평일로 보기"가 켜져 있으면 오늘 날짜는 평일로 본다.
 */
class CheckRedDayUseCase @Inject constructor(
    private val demoSettingsRepository: DemoSettingsRepository,
    private val clock: Clock,
) {

    suspend operator fun invoke(at: Instant): Boolean =
        isRedDay(at, demoSettingsRepository.observeTreatTodayAsWeekday().first())

    /** 설정이 바뀔 때마다 다시 판단할 수 있도록 판단 함수를 내보낸다 */
    fun observe(): Flow<(Instant) -> Boolean> =
        demoSettingsRepository.observeTreatTodayAsWeekday().map { treatTodayAsWeekday ->
            { at: Instant -> isRedDay(at, treatTodayAsWeekday) }
        }

    private fun isRedDay(at: Instant, treatTodayAsWeekday: Boolean): Boolean {
        val date = at.atZone(clock.zone).toLocalDate()
        if (treatTodayAsWeekday && date == LocalDate.now(clock)) return false
        return RedDayCalendar.isRedDay(date)
    }
}
