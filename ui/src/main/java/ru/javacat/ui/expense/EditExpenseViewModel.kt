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
class EditExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _editedExpense = MutableStateFlow<Expense?>(null)
    val editedExpense = _editedExpense.asStateFlow()

    fun getExpense(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                _editedExpense.emit(repository.getById(id))
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
            _loadState.emit(LoadState.Success.OK)
        }
    }

    fun saveExpense(expense: Expense){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                repository.insert(expense)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
            _loadState.emit(LoadState.Success.GoBack)
        }
    }
}