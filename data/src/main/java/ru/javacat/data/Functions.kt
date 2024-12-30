package ru.javacat.data

import android.content.Context
import retrofit2.Response
import java.net.SocketException


suspend fun <T>dbQuery(query: suspend () -> T): T {
    return try {
        query()
    } catch (e: Exception) {
        throw  DbError(e.stackTraceToString())
    }
}

sealed class AppError() : RuntimeException()
class DbError(override val message: String) : AppError()
class ApiError(val code: Int, override val message: String) : AppError()

object UnknownError : AppError()
object NetworkError : AppError()

suspend fun <T> apiRequest(request: suspend () -> Response<T>): T {
    val response = try {
        request()
    } catch (e: SocketException) {
        throw NetworkError
    } catch (e: Exception) {
        throw NetworkError
    }
    if (!response.isSuccessful) throw ApiError(response.code(), response.message())
    return response.body() ?: throw ApiError(0, "null body")
}

fun switchDatabaseModified(context: Context, boolean: Boolean){
    val sharedPreferences = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("db_modified", boolean)
    editor.apply()
}


