package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.Point

//@Entity(
//    tableName = "points_table",
//    foreignKeys = [
//        ForeignKey(
//            entity = DbOrder::class,
//            parentColumns = ["id"],
//            childColumns = ["orderId"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE
//        )
//    ]
//)
data class DbPoint(
    val id: String,
    //val orderId: String,
    val location: String,
    val arrivalDate: Long
) {
    fun toPointModel() = Point(
        id, location, arrivalDate.toLocalDate()
    )
}
