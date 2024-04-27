package ru.javacat.domain.repo

interface BaseCrud<T: Any,S : Any, Id: Any> {

    suspend fun getAll(): List<T>

    suspend fun getById(id: Id): T?

    suspend fun search(s: S): List<T>

    suspend fun insert(t: T)

    suspend fun setItem(t: T)

}