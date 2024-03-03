package ru.javacat.domain.models

data class Cargo(
    override val id: Long? = null,
    override val name: String,
    override val positionId: Long = 0L
): BaseNameModel<Long>()
