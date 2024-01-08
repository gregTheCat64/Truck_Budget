package ru.javacat.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.db.AppDb
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.LocationsDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.dao.VehiclesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun providesRoutesDao(db: AppDb): RoutesDao = db.routesDao

    @Provides
    @Singleton
    fun providesCustomerDao(db: AppDb): CustomersDao = db.customersDao

    @Provides
    @Singleton
    fun providesStaffDao(db: AppDb): StaffDao = db.staffDao

    @Provides
    @Singleton
    fun providesVehiclesDao(db: AppDb): VehiclesDao = db.vehiclesDao

    @Provides
    @Singleton
    fun providesLocationsDao(db: AppDb): LocationsDao = db.locationsDao
}