package ru.javacat.domain.models

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    sealed class Error: ApiResult<Nothing>() {
        object Unauthorized: Error()
        object ServerError: Error()
        object InsufficientStorage : Error()
        data class UnknownError(val message: String) : Error()
    }
}
