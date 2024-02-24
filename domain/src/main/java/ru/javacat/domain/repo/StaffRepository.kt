package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Staff

interface StaffRepository: BaseCrud<Staff, String>{
    val chosenDriver: StateFlow<Staff?>
}