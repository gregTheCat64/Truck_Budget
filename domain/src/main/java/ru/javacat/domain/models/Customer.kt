package ru.javacat.domain.models

data class Customer (
    val id: Long? = null,
    val name: String,
    val atiNumber: Int? = null,
    val companyPhone: String? = null,
    val formalAddress: String? = null,
    val postAddress: String? = null,
    val shortName: String? = null,
    val positionId: Long = 0L
)
