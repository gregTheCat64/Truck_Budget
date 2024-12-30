package ru.javacat.data.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.data.db.dao.ExpenseDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.Expense
import ru.javacat.domain.models.MonthlyProfit
import ru.javacat.domain.repo.ExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: ExpenseDao
): ExpenseRepository {

    override suspend fun getAll(): List<Expense> {
        return dbQuery { dao.getExpenses().map { it.toExpenseModel() } }
    }

    override suspend fun getAllExpensesByYear(year: Int): List<Expense> {
        return dbQuery { dao.getAllExpensesByYear(year.toString()).map { it.toExpenseModel() } }
    }

    override suspend fun getById(id: Long): Expense? {
        return dbQuery { dao.getExpenseById(id).toExpenseModel() }
    }

    override suspend fun getMonthlyExpenseByYear(year: String): List<MonthlyProfit> {
        return dbQuery { dao.getMonthlyExpenseByYear(year).map {
            it.toMonthlyProfitModel()
        } }
    }

    override suspend fun removeById(id: Long) {
        dbQuery {
            switchDatabaseModified(context, true)
            dao.remove(id) }
    }

    override suspend fun search(s: String): List<Expense> {
        return dbQuery { dao.searchExpense(s).map { it.toExpenseModel() } }
    }

    override suspend fun insert(t: Expense): Long {
       return dbQuery {
           switchDatabaseModified(context, true)
           dao.insertExpense(t.toDb()) }
    }

    override suspend fun setItem(t: Expense) {
        TODO("Not yet implemented")
    }

    override suspend fun clearItem() {
        TODO("Not yet implemented")
    }
}