package ru.javacat.domain.models

data class Location(
    override val id: Long? = null,
    override val nameToShow: String,
    override val positionId: Long = 0L,
): BaseNameModel<Long>()
