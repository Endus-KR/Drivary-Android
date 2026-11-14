package com.endus.drivary.di

import com.endus.drivary.data.datasource.FakeVehicleDataSource
import com.endus.drivary.data.datasource.VehicleDataSource
import com.endus.drivary.data.repository.DemoSettingsRepositoryImpl
import com.endus.drivary.data.repository.DemoVehicleRepositoryImpl
import com.endus.drivary.data.repository.TripRepositoryImpl
import com.endus.drivary.data.repository.VehicleRepositoryImpl
import com.endus.drivary.domain.repository.DemoSettingsRepository
import com.endus.drivary.domain.repository.DemoVehicleRepository
import com.endus.drivary.domain.repository.TripRepository
import com.endus.drivary.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // TODO: Pleos 연결 후 initialize() 결과에 따라 Pleos/Fake를 고른다
    @Binds
    @Singleton
    abstract fun bindVehicleDataSource(impl: FakeVehicleDataSource): VehicleDataSource

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindDemoVehicleRepository(impl: DemoVehicleRepositoryImpl): DemoVehicleRepository

    @Binds
    @Singleton
    abstract fun bindDemoSettingsRepository(impl: DemoSettingsRepositoryImpl): DemoSettingsRepository
}
