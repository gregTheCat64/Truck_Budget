package ru.javacat.data.impl

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
    private val customersDao: CustomersDao
): CustomerRepository {

    override suspend fun getCustomers(): List<Customer> {
        return dbQuery { customersDao.getCustomers().map { it.toCustomerModel() } }
    }

    override suspend fun searchCustomers(search: String): List<Customer> {
        return dbQuery { customersDao.searchCustomers(search).map { it.toCustomerModel() } }
    }

    override suspend fun getEmployeesByCustomerId(customerId: String): List<Employee> {
        val result = dbQuery {
              customersDao.getEmployeesByCustomerId(customerId).map {
                  it.toEmployeeModel()
              } }
        return result
    }

    override suspend fun insertCustomer(customer: Customer) {
        dbQuery { customersDao.insertCustomer(customer.toDb()) }
    }

    override suspend fun insertEmployee(employee: Employee) {
        dbQuery { customersDao.insertEmployee(employee.toDb()) }
    }
}