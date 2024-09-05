package ru.javacat.ui.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class RouteListViewModel @Inject constructor(
    private val repo: RouteRepository,
    private val companiesRepository: CompaniesRepository
): ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    var editedCustomer = MutableStateFlow<Company?>(null)

    val allRoutes = repo.items

    fun getAllRoutes(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAll()
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