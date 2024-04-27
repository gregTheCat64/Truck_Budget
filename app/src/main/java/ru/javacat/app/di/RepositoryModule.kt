package ru.javacat.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.impl.CargoRepositoryImpl
import ru.javacat.data.impl.CustomerRepositoryImpl
import ru.javacat.data.impl.EmployeesRepositoryImpl
import ru.javacat.data.impl.OrderRepositoryImpl
import ru.javacat.data.impl.LocationRepositoryImpl
import ru.javacat.data.impl.RouteRepositoryImpl
import ru.javacat.data.impl.StaffRepositoryImpl
import ru.javacat.data.impl.TrailersRepositoryImpl
import ru.javacat.data.impl.TrucksRepositoryImpl
import ru.javacat.domain.repo.CargoRepository
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.domain.repo.EmployeesRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.LocationRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Singleton
    @Binds
    fun bindsDraftRepository(impl: OrderRepositoryImpl): OrderRepository

    @Singleton
    @Binds
    fun bindsCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Singleton
    @Binds
    fun bindsEmployeeRepository(impl: EmployeesRepositoryImpl): EmployeesRepository

    @Singleton
    @Binds
    fun bindsStaffRepository(impl: StaffRepositoryImpl): TruckDriversRepository

    @Singleton
    @Binds
    fun bindsTrucksRepository(impl: TrucksRepositoryImpl): TrucksRepository

    @Singleton
    @Binds
    fun bindsTrailersRepository(impl: TrailersRepositoryImpl): TrailersRepository


    @Singleton
    @Binds
    fun bindsLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Singleton
    @Binds
    fun bindsCargoRepository(impl: CargoRepositoryImpl): CargoRepository
}