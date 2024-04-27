package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    val repository: CustomerRepository
): ViewModel() {

    val homeCustomer = MutableStateFlow<Customer?>(null)

    fun getHomeCustomer(){
        viewModelScope.launch(Dispatchers.IO){
            //зарезервированный id для пользователя
            val result = repository.getById(-1)
            homeCustomer.emit(result)
        }
    }
}