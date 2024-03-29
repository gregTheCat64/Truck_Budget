package ru.javacat.domain.models

import java.time.LocalDate

data class Staff (
    override val id: Long,
    val firstName: String?,
    val middleName: String?,
    val surname: String,
    val passportSerial: String,
    val passportNumber: String,
    val passportReceivedDate: String?,
    val passportReceivedPlace: String?,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?,
    val phoneNumber: String?,
    override val positionId: Long,
    override val name: String = surname
): BaseNameModel<Long>()
