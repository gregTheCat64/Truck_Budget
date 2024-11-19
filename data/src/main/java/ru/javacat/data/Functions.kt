package ru.javacat.data



suspend fun <T>dbQuery(query: suspend () -> T): T {
    return try {
        query()
    } catch (e: Exception) {
        throw  DbError(e.stackTraceToString())
    }
}

sealed class AppError() : RuntimeException()
class DbError(override val message: String) : AppError()

object UnknownError : AppError()

