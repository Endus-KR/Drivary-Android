package com.endus.drivary.domain.usecase

import com.endus.drivary.domain.repository.DemoSettingsRepository
import javax.inject.Inject
import kotlin.time.Duration

class SetConfirmDelayUseCase @Inject constructor(
    private val demoSettingsRepository: DemoSettingsRepository,
) {
    suspend operator fun invoke(delay: Duration): Result<Unit> = runCatching {
        require(delay.isPositive()) { "확정 대기 시간은 0보다 커야 한다: $delay" }
        demoSettingsRepository.setConfirmDelay(delay)
    }
}
