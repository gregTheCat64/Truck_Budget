package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.domain.repo.LocationRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val locationRepository: LocationRepository
):ViewModel() {
    private val _points = MutableStateFlow<List<Point>?>(null)
    val points = _points.asStateFlow()

    private val pointList = mutableListOf<Point>()

    private val _customers = MutableStateFlow<List<Customer>?>(null)
    val customers = _customers.asStateFlow()

    private val _locations = MutableStateFlow<List<Location>?>(null)
    val locations = _locations.asStateFlow()

    private val _pointDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val pointDate: StateFlow<LocalDate> = _pointDate

    fun setPointDate(localDate: LocalDate){
        viewModelScope.launch {
            _pointDate.emit(localDate)
        }
    }

    fun increaseDay(){
        viewModelScope.launch{
            _pointDate.emit(_pointDate.value.plusDays(1))
        }
    }

    fun decreaseDay(){
        viewModelScope.launch{
            _pointDate.emit(_pointDate.value.minusDays(1))
        }
    }

    fun getCustomers(){
        viewModelScope.launch {
            val result = customerRepository.getCustomers()
            _customers.emit(result)
        }
    }
    fun searchCustomers(search: String) {
        viewModelScope.launch {
            val result = customerRepository.searchCustomers(search)
            _customers.emit(result)
        }
    }

    fun addPoint(point: Point) {
        pointList.add(point)
        viewModelScope.launch {
            _points.emit(pointList.toList())
        }
        Log.i("MYTAG", "points: ${points.value}")
    }

    fun removePoint(point: Point) {
        pointList.remove(point)
        viewModelScope.launch {
            _points.emit(pointList.toList())
        }
    }

    fun insertNewLocation(location: Location){
        viewModelScope.launch(Dispatchers.IO){
            locationRepository.insertLocation(location)
        }
    }

    fun getLocations(){
        viewModelScope.launch(Dispatchers.IO){
            val result = locationRepository.getLocations()
            println("locations: $result")
            _locations.emit(result)
        }
    }

    fun searchLocations(search: String){
        viewModelScope.launch(Dispatchers.IO){
            val result = locationRepository.searchLocations(search)
            println("locations: $result")
            _locations.emit(result)
        }
    }
}