package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.DemoSettingsLocalDataSource
import com.endus.drivary.domain.repository.DemoSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration

class DemoSettingsRepositoryImpl @Inject constructor(
    private val demoSettingsLocalDataSource: DemoSettingsLocalDataSource,
) : DemoSettingsRepository {

    override fun observeConfirmDelay(): Flow<Duration> =
        demoSettingsLocalDataSource.observeConfirmDelay()

    override suspend fun setConfirmDelay(delay: Duration) =
        demoSettingsLocalDataSource.setConfirmDelay(delay)
}
