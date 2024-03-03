package ru.javacat.domain.models

data class Customer (
    override val id: Long? = null,
    override val name: String,
    override val positionId: Long = 0L,
    val atiNumber: Int? = null,
    val companyPhone: String? = null,
    val formalAddress: String? = null,
    val postAddress: String? = null,
    val shortName: String? = null,

): BaseNameModel<Long>()
