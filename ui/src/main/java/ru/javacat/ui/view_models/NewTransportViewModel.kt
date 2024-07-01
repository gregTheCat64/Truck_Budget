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
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.domain.use_case.SetTrailerUseCase
import ru.javacat.domain.use_case.SetTruckUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewTransportViewModel @Inject constructor(
    private val trucksRepository: TrucksRepository,
    private val trailersRepository: TrailersRepository,
    private val setTruckUseCase: SetTruckUseCase,
    private val setTrailerUseCase: SetTrailerUseCase,
): ViewModel() {

    val editedTruck = MutableStateFlow<Truck?>(null)
    val editedTrailer = MutableStateFlow<Trailer?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    suspend fun insertNewTruck(truck: Truck, isNeedToSet: Boolean) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                trucksRepository.insert(truck)
                if (isNeedToSet){
                    setTruckUseCase.invoke(truck)
                }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

    suspend fun insertNewTrailer(trailer: Trailer, isNeedToSet: Boolean) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                trailersRepository.insert(trailer)
                if (isNeedToSet){
                    setTrailerUseCase.invoke(trailer)
                }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun getTruckById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val truck = trucksRepository.getById(id)
                editedTruck.emit(truck)
                Log.i("newTransportVm", "result: $truck")
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun getTrailerById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val trailer = trailersRepository.getById(id)
                editedTrailer.emit(trailer)
                Log.i("newTransportVm", "result: $trailer")
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun removeTruckById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                trucksRepository.removeById(id)
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun removeTrailerById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                trailersRepository.removeById(id)
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}