package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Staff

@Entity(tableName = "staff_table")
data class DbStaff (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val firstName: String?,
    val middleName: String?,
    val surName: String,
    val passportSerial: String,
    val passportNumber: String,
    val passportReceivedDate: String?,
    val passportReceivedPlace: String?,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?,
    val phoneNumber: String?
){
    fun toStaff() = Staff(
        id,firstName,middleName, surName, passportSerial, passportNumber, passportReceivedDate, passportReceivedPlace, driveLicenseNumber, placeOfRegistration, phoneNumber, 0
    )
}