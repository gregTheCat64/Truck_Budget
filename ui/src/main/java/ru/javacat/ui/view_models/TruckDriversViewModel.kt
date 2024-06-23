package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

@HiltViewModel
class TruckDriversViewModel @Inject constructor(
    private val truckDriverRepo: TruckDriversRepository,
    companiesRepository: CompaniesRepository
): ViewModel() {

    private val _drivers = MutableSharedFlow<List<TruckDriver>?>(2,2)
    val drivers = _drivers.asSharedFlow()

    val currentCompany = companiesRepository.chosenItem

    fun getDriverList(companyId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = truckDriverRepo.getByCompanyId(companyId)
            _drivers.emit(result)
        }
    }
}