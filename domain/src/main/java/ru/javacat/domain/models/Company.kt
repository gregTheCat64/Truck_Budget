package ru.javacat.domain.models

data class Company (
    override val id: Long = 0L,
    override val nameToShow: String,
    override val isHidden: Boolean = false,
    val atiNumber: Int? = null,
    val managers: List<Manager>? = null,
    val companyPhone: String? = null,
    val formalAddress: String? = null,
    val postAddress: String? = null,
    val shortName: String? = nameToShow,
    override val positionId: Long = 0L,
    val isFavorite: Boolean = false,
): BaseNameModel<Long>()
