package ru.javacat.ui.view_models

import android.util.Log
import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Point
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: CustomerRepository
):ViewModel() {
    private val _points = MutableStateFlow<List<Point>?>(null)
    val points = _points.asStateFlow()

    private val pointList = mutableListOf<Point>()

    private val _customers = MutableStateFlow<List<Customer>?>(null)
    val customers = _customers.asStateFlow()

    fun getCustomers(search: String) {
        viewModelScope.launch {
            val result = repository.getCustomers(search)
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
}