package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow

interface BaseChooseItemRepository<T: Any,S : Any, Id: Any> {

    val chosenItem: StateFlow<T?>

    suspend fun getAll(): List<T>

    suspend fun getById(id: Id): T?

    suspend fun search(s: S): List<T>

    suspend fun insert(t: T)

    suspend fun setItem(t: T)

}
