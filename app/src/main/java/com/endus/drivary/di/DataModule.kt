package com.endus.drivary.di

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
