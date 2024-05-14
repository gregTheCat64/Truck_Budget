package ru.javacat.data.db.models

import androidx.room.Relation
import ru.javacat.data.db.entities.DbManager
import ru.javacat.domain.models.Partner

data class DbCustomerWithManagers(
    val id: Long,
    val companyName: String,
    val atiNumber: Int?,
    val companyPhone: String?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?,
    val positionId: Int,

    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val managers: List<DbManager>
) {
    fun toCustomerModel() = Partner(
        id,
        companyName,
        atiNumber,
        managers.map { it.toManagerModel() },
        companyPhone,
        formalAddress, postAddress, shortName
    )
}
