package ru.javacat.data.impl

import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.StaffRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepositoryImpl @Inject constructor(
    private val staffDao: StaffDao
): StaffRepository {
    override suspend fun insertDriver(driver: Staff) {
        dbQuery { staffDao.insert(driver.toDb()) }
    }
}