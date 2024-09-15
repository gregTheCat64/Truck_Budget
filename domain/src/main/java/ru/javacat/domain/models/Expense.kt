package ru.javacat.domain.models

import java.time.LocalDate

data class Expense (
    override val id: Long,
    override val nameToShow: String,
    override val isHidden: Boolean = false,
    override val positionId: Long,
    val description: String? = null,
    val date: LocalDate,
    val price: Int
): BaseNameModel<Long>()