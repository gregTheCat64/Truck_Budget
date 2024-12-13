package ru.javacat.ui.companies

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject

@HiltViewModel
class CompanyListViewModel @Inject constructor(
    private val repo: CompaniesRepository
): ViewModel() {
    private var _customers = MutableStateFlow(emptyList<Company>())
    val customers: Flow<List<Company>>
        get() = _customers
    private val TAG = "CompanyListVM"


    fun getAllCustomers(){
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "getAllCustomers")
           _customers.emit(repo.getAll())
        }
    }

    fun searchCustomers(s: String){
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "searchCustomers")
            _customers.emit(repo.search(s))
        }
    }
}