package com.endus.drivary.di

import com.endus.drivary.data.datasource.FakeVehicleDataSource
import com.endus.drivary.data.datasource.PleosVehicleDataSource
import com.endus.drivary.data.datasource.VehicleDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VehicleModule {

    /** Pleos 차량·에뮬레이터면 Pleos, 아니면 Fake(시연 모드)로 동작한다 */
    @Provides
    @Singleton
    fun provideVehicleDataSource(
        pleos: Provider<PleosVehicleDataSource>,
        fake: Provider<FakeVehicleDataSource>,
    ): VehicleDataSource {
        val pleosDataSource = pleos.get()
        return if (pleosDataSource.isServiceAvailable()) {
            Timber.i("Pleos Vehicle 서비스 사용")
            pleosDataSource
        } else {
            Timber.i("Pleos Vehicle 서비스 없음: 시연 모드(Fake)로 동작")
            fake.get()
        }
    }
}
