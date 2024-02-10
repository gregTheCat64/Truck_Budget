package ru.javacat.domain.models

abstract class Vehicle (
    open val id: Int,
    open val regNumber: String,
    open val vin: String?,
    open val model: String?,
    open val type: String?,
    open val yearOfManufacturing: String?,
)

data class Truck(
    override val id: Int,
    override val regNumber: String,
    override val vin: String?,
    override val model: String?,
    override val type: String?,
    override val yearOfManufacturing: String?
): Vehicle(
    id, regNumber, vin, model, type, yearOfManufacturing
)

data class Trailer(
    override val id: Int,
    override val regNumber: String,
    override val vin: String?,
    override val model: String?,
    override val type: String?,
    override val yearOfManufacturing: String?
): Vehicle(
    id, regNumber, vin, model, type, yearOfManufacturing
)
