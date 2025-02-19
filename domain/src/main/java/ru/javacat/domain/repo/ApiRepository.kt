package ru.javacat.domain.repo

import ru.javacat.domain.models.ApiResult
import ru.javacat.domain.models.User
import java.io.File


interface ApiRepository {

    suspend fun getUserInfo(token: String): User


    suspend fun uploadDatabaseFiles(

        token: String
    ): ApiResult<String>

    suspend fun downLoadDatabaseFiles(
        token: String
    ): ApiResult<String>

}