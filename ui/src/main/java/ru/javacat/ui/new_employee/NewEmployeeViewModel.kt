package ru.javacat.ui.new_employee

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
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewEmployeeViewModel @Inject constructor(
    private val repository: ManagersRepository,
    private val orderRepository: OrderRepository
): ViewModel() {

    var editedManager = MutableStateFlow<Manager?>(null)
    val editedOrder = orderRepository.editedItem

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

    fun saveNewManager(manager: Manager, isNeedToSet: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val newManagerId = repository.insert(manager)
            val newManager = manager.copy(id = newManagerId)

            if (isNeedToSet) {
                editedOrder.value?.copy(
                    manager = newManager
                )?.let {
                    orderRepository.updateEditedItem(
                        it
                    )
                }
            }
        }
    }
}