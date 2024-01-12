package ru.javacat.data

import java.util.Base64

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

fun String.toBase64(): String{
    return Base64.getEncoder().encodeToString(this.toByteArray())
}