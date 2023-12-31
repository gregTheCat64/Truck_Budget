package ru.javacat.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.impl.CustomerRepositoryImpl
import ru.javacat.data.impl.LocationRepositoryImpl
import ru.javacat.data.impl.RouteRepositoryImpl
import ru.javacat.data.impl.StaffRepositoryImpl
import ru.javacat.data.impl.VehicleRepositoryImpl
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.domain.repo.LocationRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.StaffRepository
import ru.javacat.domain.repo.VehicleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Singleton
    @Binds
    fun bindsCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Singleton
    @Binds
    fun bindsStaffRepository(impl: StaffRepositoryImpl): StaffRepository

    @Singleton
    @Binds
    fun bindsVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Singleton
    @Binds
    fun bindsLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}