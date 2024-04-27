package ru.javacat.data.impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.EmployeesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Employee
import ru.javacat.domain.repo.EmployeesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeesRepositoryImpl @Inject constructor(
    private val dao: EmployeesDao
): EmployeesRepository {

    private val _chosenEmployee = MutableStateFlow<Employee?>(null)
    override val chosenEmployee: StateFlow<Employee?>
        get() = _chosenEmployee.asStateFlow()

    override suspend fun getAll(): List<Employee> {
        return dbQuery { dao.getAll().map {
            it.toEmployeeModel() }
        }
    }

    override suspend fun search(s: String): List<Employee> {
        return dbQuery { dao.searchEmployees(s).map { it.toEmployeeModel() } }
    }

    override suspend fun insert(t: Employee) {
        dbQuery { dao.insertEmployee(t.toDb()) }
    }

    override suspend fun setItem(t: Employee) {
        _chosenEmployee.emit(t)
    }

    override suspend fun getEmployeesByCustomerId(customerId: Long): List<Employee> {
        val result = dbQuery {
            dao.getEmployeesByCustomerId(customerId).map {
                it.toEmployeeModel()
            } }
        return result
    }

    override suspend fun getById(id: Long): Employee {
        return dbQuery { dao.getById(id).toEmployeeModel() }
    }
}