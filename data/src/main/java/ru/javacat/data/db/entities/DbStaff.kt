package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Staff

@Entity(tableName = "staff_table")
data class DbStaff (
    @PrimaryKey
    val id: String,
    val fullName: String,
    val duty: String,
    val passportNumber: String,
    val passportReceivedDate: String,
    val passportReceivedPlace: String,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?
){
    fun toStaff() = Staff(
        id,fullName, duty, passportNumber, passportReceivedDate, passportReceivedPlace, driveLicenseNumber, placeOfRegistration
    )
}