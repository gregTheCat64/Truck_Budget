package ru.javacat.ui.trailer

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
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewTrailerViewModel @Inject constructor(
    private val trailersRepository: TrailersRepository,
    private val routeRepository: RouteRepository
): ViewModel() {
    val editedRoute = routeRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedTrailer = MutableStateFlow<Trailer?>(null)

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
    suspend fun insertNewTrailer(trailer: Trailer, isNeedToSet: Boolean) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newTrailerId = trailersRepository.insert(trailer)
                val newTrailer = trailer.copy(id = newTrailerId)
                Log.i("NewTransportVM", "newTrailer: $newTrailer")
                if (isNeedToSet){
                    //setTrailerUseCase.invoke(newTrailer)
                    editedRoute.value?.let {
                        routeRepository.updateEditedItem(
                            it.copy(
                                contractor = it.contractor?.copy(
                                    trailer = newTrailer
                                )
                            )
                        )
                    }
                }
                _loadState.emit(LoadState.Success.Created)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


    suspend fun hideTrailerById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                val trailer = trailersRepository.getById(id)
                trailer?.copy(isHidden = true)?.let { trailersRepository.updateTrailerToDb(it) }
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}