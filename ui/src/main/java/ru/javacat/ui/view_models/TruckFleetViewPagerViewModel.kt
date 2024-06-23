package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class TruckFleetViewPagerViewModel @Inject constructor(

    private val truckDriverRepo: TruckDriversRepository,
    private val trucksRepository: TrucksRepository,
    private val trailersRepository: TrailersRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _drivers = MutableStateFlow<List<TruckDriver>?>(null)
    val drivers = _drivers.asStateFlow()

    private val _trailers = MutableStateFlow<List<Trailer>?>(null)
    val trailers = _trailers.asStateFlow()

    private val _trucks = MutableStateFlow<List<Truck>?>(null)
    val trucks = _trucks.asStateFlow()

//    suspend fun updateCurrentCompany(id: Long) {
//        _loadState.emit(LoadState.Loading)
//        viewModelScope.launch(Dispatchers.IO){
//            try {
//                val company = companiesRepository.getById(id)
//                Log.i("VPVM", "company: $company")
//                if (company != null) {
//                    companiesRepository.setItem(company)
//                }
//                _loadState.emit(LoadState.Success.OK)
//            } catch (e: Exception){
//                _loadState.emit(LoadState.Error(e.message.toString()))
//            }
//        }
//    }

    fun getDriverList(companyId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = truckDriverRepo.getByCompanyId(companyId)
            _drivers.emit(result)
        }
    }

    fun getTruckList(companyId: Long){
        viewModelScope.launch(Dispatchers.IO){
            val result = trucksRepository.getByCompanyId(companyId)
            _trucks.emit(result)
        }
    }

    fun getTrailersList(companyId: Long){
        viewModelScope.launch(Dispatchers.IO){
            val result = trailersRepository.getByCompanyId(companyId)
            _trailers.emit(result)
        }
    }
}