package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.PayType

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
    @PrimaryKey
        (autoGenerate = true)
    val id: Long,
    @ColumnInfo(index = true)
    val routeId: Long,
    @TypeConverters(PointConverter::class)
    val points: List<DbPoint>,
    val date: String,
    val price: Int,
    val payType: PayType,
    val contractorPrice: Int?,
    val payTypeToContractor: PayType?,
    val commission: Int?,
    val customerId: Long,
    val managerId: Long?,
    val contractorId: Long?,
    val driverId: Long,
    val truckId: Long,
    val trailerId: Long?,
    @Embedded
    val cargo: Cargo,
    val extraConditions: String?,
    val daysToPay: Int?,
    val daysToPayToContractor: Int?,
    val paymentDeadline: String?,
    val sentDocsNumber: String?,
    val docsReceived: String?,
    //@TypeConverters(StatusConverter::class)
    val isPaidByCustomer: Boolean,
    val isPaidToContractor: Boolean
)

//class StatusConverter {
//    @TypeConverter
//    fun toStatus(value: String) = enumValueOf<OrderStatus>(value)
//    @TypeConverter
//    fun fromStatus(value: OrderStatus) = value.name
//}

class PointConverter {
    @TypeConverter
    fun fromPoints(list: List<DbPoint>): String {
        return Gson().toJson(list)
    }
    @TypeConverter
    fun toPoints(json: String): List<DbPoint> {
        val type = object: TypeToken<List<DbPoint>>() {}.type
        return Gson().fromJson(json, type)
    }
}
