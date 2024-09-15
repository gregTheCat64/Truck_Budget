package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.SalaryParameters
import ru.javacat.domain.models.TruckDriver
import java.time.LocalDate

@Entity(tableName = "truck_drivers_table")
data class DbTruckDriver (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val positionId: Long,
    val isHidden: Boolean,
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
    val comment: String?,
    @Embedded
    val salaryParameters: SalaryParameters?
){
    fun toTruckDriverModel() = TruckDriver(
        id,
        positionId,
        isHidden,
        companyId,
        firstName,
        middleName,
        surName,
        passportNumber,
        passportReceivedDate?.toLocalDate(),
        passportReceivedPlace,
        driveLicenseNumber,
        placeOfRegistration,
        phoneNumber,
        secondNumber,
        comment,
        salaryParameters
    )
}