package ru.javacat.ui.route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.ApiRepository
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class RouteListViewModel @Inject constructor(
    private val repo: RouteRepository,
    private val companiesRepository: CompaniesRepository,
    private val apiRepository: ApiRepository
): ViewModel() {
    private val TAG = "RoutesListVM"

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    var editedCustomer = MutableStateFlow<Company?>(null)

    val allRoutes = repo.items

    fun getAllRoutes(year: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllByYear(year)
        }
    }

    fun uploadBdToYandexDisk(
        token: String,
        onComplete: (Boolean) -> Unit
    ) {
        Log.i(TAG, "uploading")
        viewModelScope.launch {
            val result = apiRepository.uploadDatabaseFiles(token)
            onComplete(result)
        }
    }

   fun getCustomerById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedCustomer.emit(companiesRepository.getById(id))
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


  fun removeRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            repo.removeById(id)
        }
    }


}