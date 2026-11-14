package com.endus.drivary.data.repository

import com.endus.drivary.data.datasource.DriverLocalDataSource
import com.endus.drivary.domain.model.Driver
import com.endus.drivary.domain.repository.DriverRepository
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val driverLocalDataSource: DriverLocalDataSource,
) : DriverRepository {

    override suspend fun getDriver(): Driver = driverLocalDataSource.getDriver()
}
