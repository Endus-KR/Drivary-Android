package com.endus.drivary.di

import com.endus.drivary.data.repository.DemoSettingsRepositoryImpl
import com.endus.drivary.data.repository.DemoVehicleRepositoryImpl
import com.endus.drivary.data.repository.DriverRepositoryImpl
import com.endus.drivary.data.repository.PlaceRepositoryImpl
import com.endus.drivary.data.repository.TripRepositoryImpl
import com.endus.drivary.data.repository.VehicleRepositoryImpl
import com.endus.drivary.data.repository.WorkPlaceRepositoryImpl
import com.endus.drivary.domain.repository.DemoSettingsRepository
import com.endus.drivary.domain.repository.DemoVehicleRepository
import com.endus.drivary.domain.repository.DriverRepository
import com.endus.drivary.domain.repository.PlaceRepository
import com.endus.drivary.domain.repository.TripRepository
import com.endus.drivary.domain.repository.VehicleRepository
import com.endus.drivary.domain.repository.WorkPlaceRepository
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

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    @Singleton
    abstract fun bindDriverRepository(impl: DriverRepositoryImpl): DriverRepository

    @Binds
    @Singleton
    abstract fun bindWorkPlaceRepository(impl: WorkPlaceRepositoryImpl): WorkPlaceRepository
}
