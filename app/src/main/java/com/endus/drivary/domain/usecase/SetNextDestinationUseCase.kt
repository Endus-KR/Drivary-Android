package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoSettingsRepository
import javax.inject.Inject

class SetNextDestinationUseCase @Inject constructor(
    private val demoSettingsRepository: DemoSettingsRepository,
) {
    suspend operator fun invoke(placeId: Long): Result<Unit> = runCatching {
        demoSettingsRepository.setNextDestinationId(placeId)
    }
}
