package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.TruckDriver

@Entity(tableName = "truck_drivers_table")
data class DbTruckDriver (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val positionId: Long,
    val companyId: Long,
    val firstName: String?,
    val middleName: String?,
    val surName: String,
    val passportNumber: String?,
    val passportReceivedDate: String?,
    val passportReceivedPlace: String?,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?,
    val phoneNumber: String?,
    val secondNumber: String?,
    val comment: String?
){
    fun toTruckDriverModel() = TruckDriver(
        id,
        positionId,
        companyId,
        firstName,
        middleName,
        surName,
        passportNumber,
        passportReceivedDate,
        passportReceivedPlace,
        driveLicenseNumber,
        placeOfRegistration,
        phoneNumber,
        secondNumber,
        comment
    )
}