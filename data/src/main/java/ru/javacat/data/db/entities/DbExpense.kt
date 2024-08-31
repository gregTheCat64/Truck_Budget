package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.Expense
import java.time.LocalDate

@Entity(
    tableName = "expenses_table"
)
data class DbExpense(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val position: Long,
    val description: String? = null,
    val date: String,
    val price: Int
) {
    fun toExpenseModel() = Expense(
        id, name,position, description, date.toLocalDate(), price
    )
}