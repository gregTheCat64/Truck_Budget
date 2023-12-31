package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.domain.models.Employee

data class CustomersWithEmployees(
    @Embedded
    val customer: DbCustomer,

    @Relation(
        parentColumn = "atiNumber",
        entityColumn = "customerAtiNumber"
    )
    val employees: List<DbEmployee>
)
