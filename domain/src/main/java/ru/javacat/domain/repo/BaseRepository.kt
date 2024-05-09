package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BaseRepository<T: Any, Id: Any> {

    val items: Flow<List<T>>

    val editedItem: StateFlow<T>

    val isEdited: StateFlow<Boolean>

    suspend fun getAll()
    suspend fun getById(id: Id): T?
    suspend fun insert(t: T): Long
    suspend fun updateEditedItem(t: T)
    suspend fun removeById(id: Id)

}