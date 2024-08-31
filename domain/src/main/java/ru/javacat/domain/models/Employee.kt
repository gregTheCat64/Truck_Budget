package ru.javacat.domain.models

import java.time.LocalDate

abstract class Employee(): BaseNameModel<Long>(){
    abstract val customerId: Long
    abstract val firstName: String?
    abstract val middleName: String?
    abstract val surname: String?
    abstract val passportNumber: String?
    abstract val passportReceivedDate: LocalDate?
    abstract val passportReceivedPlace: String?
    abstract val placeOfRegistration: String?
    abstract val phoneNumber: String?
    abstract val secondNumber: String?
    abstract val comment: String?
}

data class Manager(
    override val id: Long,
    override val positionId: Long,
    override val customerId: Long,
    override val firstName: String,
    override val middleName: String?,
    override val surname: String?,
    override val passportNumber: String?,
    override val passportReceivedDate: LocalDate?,
    override val passportReceivedPlace: String?,
    override val placeOfRegistration: String?,
    override val phoneNumber: String?,
    override val secondNumber: String?,
    val email: String?,
    override val comment: String?,
): Employee(){
    override val nameToShow: String
        get() = firstName
}

data class TruckDriver(
    override val id: Long,
    override val positionId: Long,
    override val customerId: Long,
    override val firstName: String? = null,
    override val middleName: String? = null,
    override val surname: String,
    override val passportNumber: String? = null,
    override val passportReceivedDate: LocalDate? = null,
    override val passportReceivedPlace: String? = null,
    override val placeOfRegistration: String? = null,
    val driveLicenseNumber: String? = null,
    override val phoneNumber: String? = null,
    override val secondNumber: String? = null,
    override val comment: String? = null,
    val salaryParameters: SalaryParameters?
): Employee(){
    override val nameToShow: String
        get() = "$surname $firstName"
}
