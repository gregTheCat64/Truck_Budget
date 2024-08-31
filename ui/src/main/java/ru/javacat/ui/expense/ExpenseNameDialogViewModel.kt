package ru.javacat.ui.expense

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.ExpenseRepository
import javax.inject.Inject

@HiltViewModel
class ExpenseNameDialogViewModel @Inject constructor(
    private val repository: ExpenseRepository
): ViewModel() {

    private val _expenseNamesList = MutableStateFlow<List<String>?>(null)
    val expenseNamesList = _expenseNamesList.asStateFlow()

    fun getExpenseNames(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAll()
            val uniqueNames = result.map{
                it.nameToShow
            }.distinct()
            _expenseNamesList.emit(uniqueNames)
            Log.i("ExpenseNameDialogVM", "uniqueNames: $uniqueNames")
        }
    }

    fun searchExpense(s: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.search(s).map {
                it.nameToShow
            }.distinct()

            _expenseNamesList.emit(result)
            Log.i("ExpenseNameDialogVM", "searchNames: $result")
        }
    }
}