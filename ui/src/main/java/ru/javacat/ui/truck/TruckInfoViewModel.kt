package ru.javacat.ui.truck

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Truck
import ru.javacat.domain.use_case.GetTruckUseCase
import ru.javacat.domain.use_case.HideTruckUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class TruckInfoViewModel @Inject constructor(
    private val getTruckUseCase: GetTruckUseCase,
    private val hideTruckUseCase: HideTruckUseCase
) : ViewModel() {
    val editedTruck = MutableStateFlow<Truck?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getTruck(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                val truck = getTruckUseCase.invoke(id)
                editedTruck.emit(truck)
                Log.i("newTransportVm", "result: $truck")
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun hideTruck(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                hideTruckUseCase.invoke(id)
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

}