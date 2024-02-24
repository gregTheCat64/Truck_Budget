package ru.javacat.domain.models

abstract class Vehicle {
    abstract val id: Int
    abstract val regNumber: String
    abstract val vin: String?
    abstract val model: String?
    abstract val type: String?
    abstract val yearOfManufacturing: String?
    abstract val positionId: Long
}

data class Truck(
    override val id: Int,
    override val regNumber: String,
    override val vin: String?,
    override val model: String?,
    override val type: String?,
    override val yearOfManufacturing: String?,
    override val positionId: Long = 0L
): Vehicle()

data class Trailer(
    override val id: Int,
    override val regNumber: String,
    override val vin: String?,
    override val model: String?,
    override val type: String?,
    override val yearOfManufacturing: String?,
    override val positionId: Long = 0L
): Vehicle()
