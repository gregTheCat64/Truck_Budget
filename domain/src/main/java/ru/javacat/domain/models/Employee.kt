package ru.javacat.domain.models

import java.time.LocalDate

abstract class Employee(): BaseNameModel<Long>(){
    abstract val companyId: Long
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
    override val positionId: Long = 0,
    override val isHidden: Boolean = false,
    override val companyId: Long,
    override val firstName: String,
    override val middleName: String? = null,
    override val surname: String? = null,
    override val passportNumber: String? = null,
    override val passportReceivedDate: LocalDate? = null,
    override val passportReceivedPlace: String? = null,
    override val placeOfRegistration: String? = null,
    override val phoneNumber: String? = null,
    override val secondNumber: String? = null,
    val email: String? = null,
    override val comment: String? = null,
): Employee(){
    override val nameToShow: String
        get() = firstName
}

data class TruckDriver(
    override val id: Long,
    override val positionId: Long = 0L,
    override val isHidden: Boolean = false,
    override val companyId: Long,
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
    val salaryParameters: SalaryParameters? = null
): Employee() {
    override val nameToShow: String
        get() =
            "$surname ${firstName?:""}"

}


