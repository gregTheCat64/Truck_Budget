package ru.javacat.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Partner
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val dao: CustomersDao
): CustomerRepository {

    override val chosenItem: StateFlow<Partner?>
        get() = _chosenCustomer.asStateFlow()
    private val _chosenCustomer = MutableStateFlow<Partner?>(null)
//    override val chosenCustomer: StateFlow<Customer?>
//        get() = _chosenCustomer.asStateFlow()

    private var _customers = MutableStateFlow(emptyList<Partner>())
    override val customers: Flow<List<Partner>>
        get() = _customers

    override suspend fun getAll(): List<Partner> {
        _customers.emit(dao.getAll().map { it.toCustomerModel() })
        return dbQuery { dao.getCustomers().map { it.toCustomerModel() } }
    }

    override suspend fun getById(id: Long): Partner? {
        return dbQuery { dao.getById(id)?.toCustomerModel()}
    }

    override suspend fun search(s: String): List<Partner> {
        return dbQuery { dao.searchCustomers(s).map { it.toCustomerModel() } }
    }

    override suspend fun insert(t: Partner) {
        dbQuery { dao.insertCustomer(t.toDb()) }
    }

    override suspend fun setItem(t: Partner) {
        _chosenCustomer.emit(t)
    }

}