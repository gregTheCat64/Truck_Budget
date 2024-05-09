package ru.javacat.domain.models

abstract class Employee(): BaseNameModel<Long>(){
    abstract val customerId: Long
    abstract val firstName: String?
    abstract val middleName: String?
    abstract val surname: String?
    abstract val passportNumber: String?
    abstract val passportReceivedDate: String?
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
    override val passportReceivedDate: String?,
    override val passportReceivedPlace: String?,
    override val placeOfRegistration: String?,
    override val phoneNumber: String?,
    override val secondNumber: String?,
    val email: String?,
    override val comment: String?
): Employee(){
    override val name: String
        get() = firstName
}

data class TruckDriver(
    override val id: Long,
    override val positionId: Long,
    override val customerId: Long,
    override val firstName: String?,
    override val middleName: String?,
    override val surname: String,
    override val passportNumber: String?,
    override val passportReceivedDate: String?,
    override val passportReceivedPlace: String?,
    override val placeOfRegistration: String?,
    val driveLicenseNumber: String?,
    override val phoneNumber: String?,
    override val secondNumber: String?,
    override val comment: String?,
): Employee(){
    override val name: String
        get() = surname
}
