package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoSettingsRepository
import javax.inject.Inject

class SetTreatTodayAsWeekdayUseCase @Inject constructor(
    private val demoSettingsRepository: DemoSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = runCatching {
        demoSettingsRepository.setTreatTodayAsWeekday(enabled)
    }
}
