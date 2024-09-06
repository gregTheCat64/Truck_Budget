package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.javacat.data.db.entities.DbExpense
import ru.javacat.data.db.models.DbMonthlyProfit
import ru.javacat.domain.models.Expense

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses_table")
    suspend fun getExpenses(): List<DbExpense>

    @Query("SELECT * FROM expenses_table WHERE strftime('%Y', date) = :year")
    fun getAllExpensesByYear(year: String): List<DbExpense>

    @Query("SELECT * FROM expenses_table WHERE id =:id")
    suspend fun getExpenseById(id: Long): DbExpense

    @Query("SELECT * FROM expenses_table WHERE name LIKE '%' || :search || '%'")
    suspend fun searchExpense(search: String): List<DbExpense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: DbExpense): Long

    @Query("""
        SELECT strftime('%m', date) as monthDate,
        SUM(price) as totalProfit
        FROM expenses_table
        WHERE strftime('%Y', date) = :year
        GROUP BY monthDate
        ORDER BY monthDate
    """)
    fun getMonthlyExpenseByYear(year: String): List<DbMonthlyProfit>
}