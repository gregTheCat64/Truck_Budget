package ru.javacat.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.db.AppDb
import ru.javacat.data.db.dao.CargoDao
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.LocationsDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.dao.TrailersDao
import ru.javacat.data.db.dao.TrucksDao
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
    fun providesTrucksDao(db: AppDb): TrucksDao = db.trucksDao

    @Provides
    @Singleton
    fun providesTrailersDao(db: AppDb): TrailersDao = db.trailersDao

    @Provides
    @Singleton
    fun providesLocationsDao(db: AppDb): LocationsDao = db.locationsDao

    @Provides
    @Singleton
    fun providesCargoDao(db: AppDb): CargoDao = db.cargoDao
}