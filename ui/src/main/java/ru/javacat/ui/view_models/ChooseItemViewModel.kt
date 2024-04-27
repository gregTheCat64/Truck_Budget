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
import ru.javacat.domain.models.CargoName
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ChooseItemViewModel @Inject constructor(
    private val trucksRepository: TrucksRepository,
    private val trailersRepository: TrailersRepository,
    private val staffRepository: TruckDriversRepository,
    private val routeRepository: RouteRepository,
): ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _trucks = MutableStateFlow<List<Truck>?>(null)
    val trucks = _trucks.asStateFlow()

    private val _trailers = MutableStateFlow<List<Trailer>?>(null)
    val trailers = _trailers.asStateFlow()

//    private val _customers = MutableStateFlow<List<Customer>?>(null)
//    val customers = _customers.asStateFlow()

    private val _drivers = MutableStateFlow<List<TruckDriver>?>(null)
    val drivers = _drivers.asStateFlow()

    private val _cargo = MutableStateFlow<List<CargoName>?>(null)
    val cargo = _cargo.asStateFlow()

//    val chosenTruck = trucksRepository.chosenTruck
//
//    val chosenTrailer = trailersRepository.chosenTrailer
//
//    val chosenDriver = staffRepository.chosenDriver

    val editedRoute = routeRepository.editedRoute

 //   val editedOrder = orderRepository.editedOrder


    fun getTrucks(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = trucksRepository.getAll()
            _trucks.emit(result)
        }
    }

    fun getTrailers(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = trailersRepository.getAll()
            _trailers.emit(result)
        }
    }

    fun getDriver(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = staffRepository.getAll()
            _drivers.emit(result)
        }
    }

//    fun getCustomer(){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = customerRepository.getAll()
//            _customers.emit(result)
//        }
//    }

    fun searchTrucks(s: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = trucksRepository.search(s)
            _trucks.emit(result)
        }
    }

    fun searchTrailers(s: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = trailersRepository.search(s)
            _trailers.emit(result)
        }
    }

    fun searchStaff(s: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = staffRepository.search(s)
            _drivers.emit(result)
        }
    }

    fun setTruck(t: Truck){
        viewModelScope.launch {
            val newRoute = editedRoute.value.copy(truck = t)
            Log.i("ChooseItemVM", "newRoute: $newRoute")
            routeRepository.updateEditedRoute(newRoute)
        }
    }

    fun setTrailer(t: Trailer){
        viewModelScope.launch {
            editedRoute.value?.copy(trailer = t)?.let { routeRepository.updateEditedRoute(it) }
        }
    }

    fun setDriver(t: TruckDriver){
        viewModelScope.launch {
            editedRoute.value?.copy(driver = t)?.let { routeRepository.updateEditedRoute(it) }
        }
    }

//    fun setCustomer(t: Customer){
//        viewModelScope.launch{
//            orderRepository.updateOrder(editedOrder.value.copy(customer = t))
//        }
//    }
}