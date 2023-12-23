package ru.javacat.data.db.models

import androidx.room.Relation
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.domain.models.Customer

data class OrdersWithPointsAndCustomer (
    val order: DbOrder,

    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val points: List<DbPoint>,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "atiNumber"
    )
    val customer: DbCustomer
)