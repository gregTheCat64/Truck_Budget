package ru.javacat.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.impl.ApiRepositoryImpl
import ru.javacat.data.impl.CargoRepositoryImpl
import ru.javacat.data.impl.CompaniesRepositoryImpl
import ru.javacat.data.impl.ExpenseRepositoryImpl
import ru.javacat.data.impl.ManagersRepositoryImpl
import ru.javacat.data.impl.OrderRepositoryImpl
import ru.javacat.data.impl.LocationRepositoryImpl
import ru.javacat.data.impl.RouteRepositoryImpl
import ru.javacat.data.impl.TruckDriversRepositoryImpl
import ru.javacat.data.impl.TrailersRepositoryImpl
import ru.javacat.data.impl.TrucksRepositoryImpl
import ru.javacat.domain.repo.ApiRepository
import ru.javacat.domain.repo.CargoRepository
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.ExpenseRepository
import ru.javacat.domain.repo.ManagersRepository
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
    fun bindsCustomerRepository(impl: CompaniesRepositoryImpl): CompaniesRepository

    @Singleton
    @Binds
    fun bindsManagerRepository(impl: ManagersRepositoryImpl): ManagersRepository

    @Singleton
    @Binds
    fun bindsTruckDriversRepository(impl: TruckDriversRepositoryImpl): TruckDriversRepository

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

    @Singleton
    @Binds
    fun bindsExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Singleton
    @Binds
    fun bindsApiRepository(impl: ApiRepositoryImpl): ApiRepository
}