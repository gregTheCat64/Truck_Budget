package ru.javacat.domain.models

abstract class Vehicle(
) : BaseNameModel<Long>(
) {
    abstract val companyId: Long
    abstract val regNumber: String
    abstract val regionCode: Int
    abstract val vin: String?
    abstract val model: String?
    abstract val type: String?
    abstract val yearOfManufacturing: String?
}

data class Truck(
    override val id: Long,
    override val companyId: Long,
    override val regNumber: String,
    override val regionCode: Int,
    override val vin: String?,
    override val model: String?,
    override val yearOfManufacturing: String?,
): Vehicle(){
    override val positionId: Long
        get() = 0L
    override val nameToShow: String
        get() = "$regNumber $regionCode"
    override val type: String
        get() = "Truck"
}

data class Trailer(
    override val id: Long,
    override val companyId: Long,
    override val regNumber: String,
    override val regionCode: Int,
    override val vin: String?,
    override val model: String?,
    override val yearOfManufacturing: String?,
    override val type: String?
    ): Vehicle(){
    override val positionId: Long
        get() = 0L
    override val nameToShow: String
        get() = "$regNumber $regionCode"
}
