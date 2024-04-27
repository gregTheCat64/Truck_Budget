package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class AddDocumentsViewModel @Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel() {
    val editedOrder = orderRepository.editedOrder

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun addDocumentsToOrder(docNumber: String){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value?.copy(sentDocsNumber = docNumber)
                    ?.let { orderRepository.updateOrder(it) }
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


}