package ru.javacat.domain.models

data class CargoName(
    override val id: Long? = null,
    override val nameToShow: String,
    override val positionId: Long = 0L
): BaseNameModel<Long>()
