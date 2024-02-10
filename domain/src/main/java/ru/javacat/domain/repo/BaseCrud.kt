package ru.javacat.domain.repo

interface BaseCrud<T: Any> {
    suspend fun getAll(): List<T>

    suspend fun search(string: String): List<T>

    suspend fun insert(t: T)

}