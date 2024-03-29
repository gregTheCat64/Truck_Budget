package ru.javacat.domain.models

data class Customer (
    override val id: Long? = null,
    override val name: String,

    val atiNumber: Int? = null,
    val companyPhone: String? = null,
    val formalAddress: String? = null,
    val postAddress: String? = null,
    val shortName: String? = null,
    override val positionId: Long = 0L,
): BaseNameModel<Long>()
