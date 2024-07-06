package ru.javacat.ui.edit_order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Manager
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.ManagersRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class EditManagerDialogViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val companiesRepository: CompaniesRepository,
    private val managersRepository: ManagersRepository
): ViewModel() {

    val editedOrder = orderRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _managers = MutableStateFlow<List<Manager>?>(null)
    val managers = _managers.asStateFlow()
    fun getEmployee(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("EditManagerDialogVM", "customerId: $id")
            val result = managersRepository.getManagersByCustomerId(id)
            _managers.emit(result)
        }
    }

    fun searchEmployee(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = managersRepository.search(search)
            _managers.emit(result)
        }
    }

    fun addManagerToOrder(manager: Manager){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value?.copy(
                    manager = manager
                )
                    .let {
                        if (it != null) {
                            orderRepository.updateEditedItem(it)
                        }
                    }
                _loadState.emit(LoadState.Success.OK)

            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}