package ru.javacat.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Expense
import ru.javacat.domain.repo.ExpenseRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val repository: ExpenseRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _expenses = MutableStateFlow<List<Expense>?>(null)
    val expenses = _expenses.asStateFlow()

    fun getExpenseList(){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                _expenses.emit(repository.getAll())
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
            _loadState.emit(LoadState.Success.OK)
        }
    }
}