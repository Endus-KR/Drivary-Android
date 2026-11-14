package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTreatTodayAsWeekdayUseCase @Inject constructor(
    private val demoSettingsRepository: DemoSettingsRepository,
) {
    operator fun invoke(): Flow<Boolean> = demoSettingsRepository.observeTreatTodayAsWeekday()
}
