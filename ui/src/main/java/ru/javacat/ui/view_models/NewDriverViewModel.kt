package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.StaffRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewDriverViewModel @Inject constructor(
    private val repository: StaffRepository
):ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

     suspend fun insertNewDriver(driver: Staff){
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insert(driver)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }

    }
}