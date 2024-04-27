package ru.javacat.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val dao: CustomersDao
): CustomerRepository {

    private val _chosenCustomer = MutableStateFlow<Customer?>(null)
    override val chosenCustomer: StateFlow<Customer?>
        get() = _chosenCustomer.asStateFlow()

    private var _customers = MutableStateFlow(emptyList<Customer>())
    override val customers: Flow<List<Customer>>
        get() = _customers

    override suspend fun getAll(): List<Customer> {
        _customers.emit(dao.getAll().map { it.toCustomerModel() })
        return dbQuery { dao.getCustomers().map { it.toCustomerModel() } }
    }

    override suspend fun getById(id: Long): Customer? {
        return dbQuery { dao.getById(id)?.toCustomerModel()}
    }

    override suspend fun search(s: String): List<Customer> {
        return dbQuery { dao.searchCustomers(s).map { it.toCustomerModel() } }
    }

    override suspend fun insert(t: Customer) {
        dbQuery { dao.insertCustomer(t.toDb()) }
    }

    override suspend fun setItem(t: Customer) {
        _chosenCustomer.emit(t)
    }

}