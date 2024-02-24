package ru.javacat.data.impl

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

    override suspend fun getAll(): List<Customer> {
        return dbQuery { dao.getCustomers().map { it.toCustomerModel() } }
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

    //    override suspend fun getCustomers(): List<Customer> {
//        return dbQuery { customersDao.getCustomers().map { it.toCustomerModel() } }
//    }
//
//    override suspend fun searchCustomers(search: String): List<Customer> {
//        return dbQuery { customersDao.searchCustomers(search).map { it.toCustomerModel() } }
//    }
//
//    override suspend fun getEmployeesByCustomerId(customerId: String): List<Employee> {
//        val result = dbQuery {
//              customersDao.getEmployeesByCustomerId(customerId).map {
//                  it.toEmployeeModel()
//              } }
//        return result
//    }
//
//    override suspend fun insertCustomer(customer: Customer) {
//        dbQuery { customersDao.insertCustomer(customer.toDb()) }
//    }
//
//    override suspend fun insertEmployee(employee: Employee) {
//        dbQuery { customersDao.insertEmployee(employee.toDb()) }
//    }
}