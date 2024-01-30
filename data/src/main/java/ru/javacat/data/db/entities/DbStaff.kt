package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Staff

@Entity(tableName = "staff_table")
data class DbStaff (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fullName: String,
    val passportSerial: String,
    val passportNumber: String,
    val passportReceivedDate: String?,
    val passportReceivedPlace: String?,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?,
    val phoneNumber: String?
){
    fun toStaff() = Staff(
        id,fullName, passportSerial, passportNumber, passportReceivedDate, passportReceivedPlace, driveLicenseNumber, placeOfRegistration, phoneNumber
    )
}