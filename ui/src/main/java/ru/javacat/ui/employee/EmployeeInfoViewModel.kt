package ru.javacat.ui.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Manager
import ru.javacat.domain.repo.ManagersRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class EmployeeInfoViewModel @Inject constructor(
    private val repository: ManagersRepository,
): ViewModel() {

    val editedManager = MutableStateFlow<Manager?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getManagerById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedManager.emit(repository.getById(id))
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun updateManagerToDb(manager: Manager){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                repository.insert(manager)
                editedManager.emit(manager)
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun remove(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                repository.removeById(id)
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}