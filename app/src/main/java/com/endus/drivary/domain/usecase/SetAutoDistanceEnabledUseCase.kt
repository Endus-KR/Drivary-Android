package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoSettingsRepository
import javax.inject.Inject

class SetAutoDistanceEnabledUseCase @Inject constructor(
    private val demoSettingsRepository: DemoSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = runCatching {
        demoSettingsRepository.setAutoDistanceEnabled(enabled)
    }
}
