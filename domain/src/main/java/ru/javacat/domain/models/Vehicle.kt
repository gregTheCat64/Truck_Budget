package ru.javacat.domain.models

abstract class Vehicle: BaseNameModel<Long>() {
    abstract val regNumber: String
    abstract val vin: String?
    abstract val model: String?
    abstract val type: String?
    abstract val yearOfManufacturing: String?

}

data class Truck(
    override val id: Long,
    override val regNumber: String,
    override val vin: String?,
    override val model: String?,
    override val type: String?,
    override val yearOfManufacturing: String?,
    override val positionId: Long = 0L,
    override val name: String = regNumber
): Vehicle()

data class Trailer(
    override val id: Long,
    override val regNumber: String,
    override val vin: String?,
    override val model: String?,
    override val type: String?,
    override val yearOfManufacturing: String?,
    override val positionId: Long = 0L,
    override val name: String = regNumber
): Vehicle()
