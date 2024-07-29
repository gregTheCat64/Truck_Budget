package ru.javacat.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.javacat.data.db.AppDb
import ru.javacat.data.db.dao.CargoDao
import ru.javacat.data.db.dao.CompaniesDao
import ru.javacat.data.db.dao.ExpenseDao
import ru.javacat.data.db.dao.ManagersDao
import ru.javacat.data.db.dao.LocationsDao
import ru.javacat.data.db.dao.OrdersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.TruckDriversDao
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
    fun providesOrdersDao(db: AppDb): OrdersDao = db.ordersDao

    @Provides
    @Singleton
    fun providesCustomerDao(db: AppDb): CompaniesDao = db.customersDao

    @Provides
    @Singleton
    fun providesManagersDao(db: AppDb): ManagersDao = db.managersDao

    @Provides
    @Singleton
    fun providesStaffDao(db: AppDb): TruckDriversDao = db.truckDriversDao

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

    @Provides
    @Singleton
    fun providesExpenseDao(db: AppDb): ExpenseDao = db.expenseDao
}