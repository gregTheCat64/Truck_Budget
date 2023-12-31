package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Vehicle
import java.time.LocalDate

@Entity(
    tableName = "orders_table",
    foreignKeys = [
        ForeignKey(
            entity = DbRoute::class,
            parentColumns = ["id"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
    )
data class DbOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(index = true)
    val routeId: String,
    //val points: List<Point>,
    @Embedded
    val driver: Staff,
    @Embedded
    val truck: Vehicle,
    @Embedded(prefix = "trailer")
    val trailer: Vehicle?,
    val price: Int,
    val customerId: Int,
    val paymentDeadline: String?,
    val sentDocsNumber: String?,
    val docsReceived: String?,
    @TypeConverters(StatusConverter::class)
    val status: OrderStatus
)

class StatusConverter {
    @TypeConverter
    fun toStatus(value: String) = enumValueOf<OrderStatus>(value)
    @TypeConverter
    fun fromStatus(value: OrderStatus) = value.name
}
