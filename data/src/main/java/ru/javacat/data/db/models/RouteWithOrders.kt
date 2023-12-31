package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute

data class RouteWithOrders (
    @Embedded
    val route: DbRoute,

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId",
        entity = DbOrder::class
    )
    val orders: List<OrderWithPointsAndCustomer>,

)