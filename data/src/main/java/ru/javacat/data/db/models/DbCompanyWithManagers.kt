package ru.javacat.data.db.models

import androidx.room.Relation
import ru.javacat.data.db.entities.DbManager
import ru.javacat.domain.models.Company

data class DbCompanyWithManagers(
    val id: Long,
    val companyName: String,
    val isHidden: Boolean = false,
    val atiNumber: Int?,
    val companyPhone: String?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?,
    val positionId: Long,
    val isFavorite: Boolean,

    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val managers: List<DbManager>
) {
    fun toCompanyModel() = Company(
        id,
        companyName,
        isHidden,
        atiNumber,
        managers.map { it.toManagerModel() },
        companyPhone,
        formalAddress,
        postAddress,
        shortName,
        positionId,
        isFavorite
    )
}
