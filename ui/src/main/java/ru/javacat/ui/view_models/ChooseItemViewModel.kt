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
import ru.javacat.domain.models.CargoName
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.domain.use_case.GetCompaniesUseCase
import ru.javacat.domain.use_case.GetTrailersByCompanyIdUseCase
import ru.javacat.domain.use_case.GetTruckDriversByCompanyIdUseCase
import ru.javacat.domain.use_case.GetTrucksByCompanyIdUseCase
import ru.javacat.domain.use_case.SetCompanyUseCase
import ru.javacat.domain.use_case.SetTrailerUseCase
import ru.javacat.domain.use_case.SetTruckDriverUseCase
import ru.javacat.domain.use_case.SetTruckUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ChooseItemViewModel @Inject constructor(
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getTruckDriversUseCase: GetTruckDriversByCompanyIdUseCase,
    private val getTrucksUseCase: GetTrucksByCompanyIdUseCase,
    private val getTrailersUseCase: GetTrailersByCompanyIdUseCase,
    private val setCompanyUseCase: SetCompanyUseCase,
    private val setTruckDriverUseCase: SetTruckDriverUseCase,
    private val setTruckUseCase: SetTruckUseCase,
    private val setTrailerUseCase: SetTrailerUseCase
): ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _contractors = MutableStateFlow<List<Company>?>(null)
    val contractors = _contractors.asStateFlow()

    private val _trucks = MutableStateFlow<List<Truck>?>(null)
    val trucks = _trucks.asStateFlow()

    private val _trailers = MutableStateFlow<List<Trailer>?>(null)
    val trailers = _trailers.asStateFlow()


    private val _drivers = MutableStateFlow<List<TruckDriver>?>(null)
    val drivers = _drivers.asStateFlow()


    fun getContractors(){
        viewModelScope.launch(Dispatchers.IO) {
            _contractors.emit(getCompaniesUseCase.invoke())
        }
    }


    fun getTrucks(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTrucksUseCase.invoke(id)
            _trucks.emit(result)
        }
    }

    fun getTrailers(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTrailersUseCase.invoke(id)
            _trailers.emit(result)
        }
    }

    fun getDriver(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTruckDriversUseCase.invoke(id)
            _drivers.emit(result)
        }
    }

    fun setCompany(t: Company){
        viewModelScope.launch {
            setCompanyUseCase.invoke(t)
        }
    }

    fun setTruck(t: Truck){
        viewModelScope.launch {
            setTruckUseCase.invoke(t)
        }
    }

    fun setTrailer(t: Trailer){
        viewModelScope.launch {
            setTrailerUseCase.invoke(t)
        }
    }

    fun setDriver(t: TruckDriver){
        viewModelScope.launch {
            setTruckDriverUseCase.invoke(t)
        }
    }

//    fun searchTrucks(s: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = trucksRepository.search(s)
//            _trucks.emit(result)
//        }
//    }
//
//    fun searchTrailers(s: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = trailersRepository.search(s)
//            _trailers.emit(result)
//        }
//    }
//
//    fun searchStaff(s: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = truckDriversRepository.search(s)
//            _drivers.emit(result)
//        }
//    }

//    fun searchContractors(search: String) {
//        viewModelScope.launch(Dispatchers.IO){
//            val result = companiesRepository.search(search)
//            _contractors.emit(result)
//        }
//    }

}