package ru.javacat.domain.repo

import ru.javacat.domain.models.Manager

interface ManagersRepository: BaseChooseItemRepository<Manager, String, Long> {
    //val chosenManager: StateFlow<Manager?>
    suspend fun getManagersByCustomerId(customerId: Long): List<Manager>
}