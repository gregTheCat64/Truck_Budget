package ru.javacat.domain.repo

import ru.javacat.domain.models.Staff

interface StaffRepository {

    suspend fun insertDriver(driver: Staff)
}