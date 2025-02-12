package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.javacat.data.db.entities.DbCompany
import ru.javacat.data.db.entities.DbManager
import ru.javacat.data.db.models.DbCompanyWithManagers

@Dao
interface CompaniesDao {

    @Transaction
    @Query("SELECT * FROM companies_table  WHERE isHidden != 1 ORDER BY isFavorite DESC,id ASC")
    fun getAll(): List<DbCompanyWithManagers>

    @Transaction
    @Query("SELECT * FROM companies_table  ORDER BY isFavorite DESC,id ASC")
    fun showWithHidden(): List<DbCompanyWithManagers>

    @Transaction
    @Query("SELECT * FROM companies_table WHERE id =:id")
    suspend fun getById(id: Long): DbCompanyWithManagers?

    @Transaction
    @Query("SELECT * FROM companies_table WHERE isHidden != 1 AND companyName LIKE '%' || :search || '%' ORDER BY isFavorite DESC,id ASC")
    suspend fun searchCustomers(search: String): List<DbCompanyWithManagers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(
        customer: DbCompany
    ): Long

    @Update()
    suspend fun updateCustomerAndEmployees(
        customer: DbCompany,
        employee: List<DbManager>
    )

    @Update
    suspend fun updateCompany(dbCompany: DbCompany)


    @Query("DELETE FROM companies_table WHERE atiNumber =:id")
    suspend fun removeCustomer(id: Int)


    //TODO: ИЗМЕНИТЬ вставку работников отдельно от клиента
    //и заказов от поездки
    //и поинты
}