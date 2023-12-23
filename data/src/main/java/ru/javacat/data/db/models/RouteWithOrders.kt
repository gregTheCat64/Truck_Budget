package ru.javacat.data.db.models

import androidx.room.Relation
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute

data class RouteWithOrders (
    val route: DbRoute,

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId"
    )
    val orders: List<DbOrder>
)