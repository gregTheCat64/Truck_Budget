package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.StaffRepository
import javax.inject.Inject

@HiltViewModel
class NewStaffViewModel @Inject constructor(
    private val repository: StaffRepository
):ViewModel() {
    suspend fun insertNewStaff(staff: Staff){
        repository.insert(staff)
    }
}