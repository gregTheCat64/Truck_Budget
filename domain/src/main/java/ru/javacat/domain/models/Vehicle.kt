package ru.javacat.domain.models

abstract class Vehicle(
) : BaseNameModel<Long>(
) {
    abstract val companyId: Long
    abstract val regNumber: String
    abstract val regionCode: Int?
    abstract val vin: String?
    abstract val model: String?
    abstract val type: String?
    abstract val yearOfManufacturing: String?
}

data class Truck(
    override val id: Long,
    override val isHidden: Boolean = false,
    override val companyId: Long,
    override val regNumber: String,
    override val regionCode: Int? = null,
    override val vin: String? = null,
    override val model: String? = null,
    override val yearOfManufacturing: String? = null,
): Vehicle(){
    override val positionId: Long
        get() = 0L
    override val nameToShow: String
        get() = "$regNumber ${regionCode?:""}"
    override val type: String
        get() = "Тягач"
}

data class Trailer(
    override val id: Long,
    override val isHidden: Boolean = false,
    override val companyId: Long,
    override val regNumber: String,
    override val regionCode: Int? = null,
    override val vin: String? = null,
    override val model: String? = null,
    override val yearOfManufacturing: String? = null,
    override val type: String? = null
    ): Vehicle(){
    override val positionId: Long
        get() = 0L
    override val nameToShow: String
        get() = "$regNumber ${regionCode?:""}"
}
