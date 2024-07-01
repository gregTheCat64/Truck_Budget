package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.use_case.SetTruckDriverUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewDriverViewModel @Inject constructor(
    private val repository: TruckDriversRepository,
    private val setTruckDriverUseCase: SetTruckDriverUseCase
):ViewModel() {

    val editedTruckDriver = MutableStateFlow<TruckDriver?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

     suspend fun insertNewDriver(driver: TruckDriver, isNeedToSet: Boolean){
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insert(driver)
                if (isNeedToSet){
                    setTruckDriverUseCase.invoke(driver)
                }
                _loadState.emit(LoadState.Success.Created)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun getTruckDriverById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedTruckDriver.emit(repository.getById(id))
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun removeDriverById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                 repository.removeById(id)
                Log.i("NewDriverVM", "deleting driver: $id")
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}