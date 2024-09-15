package ru.javacat.ui.TruckDriver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.use_case.GetTruckDriverUseCase
import ru.javacat.domain.use_case.HideTruckDriverUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class TruckDriverInfoViewModel @Inject constructor(
    private val getTruckDriverUseCase: GetTruckDriverUseCase,
    private val hideTruckDriverUseCase: HideTruckDriverUseCase
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedTruckDriver = MutableStateFlow<TruckDriver?>(null)

    fun getTruckDriver(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                editedTruckDriver.value = getTruckDriverUseCase.invoke(id)
                _loadState.emit(LoadState.Success.OK)
            } catch (e:Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

    fun hideDriver(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                hideTruckDriverUseCase.invoke(id)
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}