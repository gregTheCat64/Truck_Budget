package ru.javacat.ui.new_route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Contractor
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.GetCompaniesUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ChooseContractorViewModel @Inject constructor(
    private val getCompaniesUseCase: GetCompaniesUseCase,

    private val routeRepository: RouteRepository
): ViewModel() {

    val editedRoute = routeRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _contractors = MutableStateFlow<List<Company>?>(null)
    val contractors = _contractors.asStateFlow()

    fun getContractors() {
        viewModelScope.launch(Dispatchers.IO) {
            _contractors.emit(getCompaniesUseCase.invoke())
        }
    }

    fun setCompany(t: Company) {
        viewModelScope.launch {
            editedRoute.value?.let {
                routeRepository.updateEditedItem(
                    it.copy(
                        contractor = Contractor(
                            company = t,
                            driver = null,
                            truck = null,
                            trailer = null
                        )
                    )
                )
            }
//            setCompanyUseCase.invoke(t)
//            clearTruckUseCase.invoke()
//            clearTrailerUseCase.invoke()
//            clearTruckDriverUseCase.invoke()
        }
    }

        }
//

//


//    fun searchContractors(search: String) {
//        viewModelScope.launch(Dispatchers.IO){
//            val result = companiesRepository.search(search)
//            _contractors.emit(result)
//        }
//    }

