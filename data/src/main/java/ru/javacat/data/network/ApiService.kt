package ru.javacat.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import ru.javacat.domain.models.User

interface ApiService {
    @GET("https://login.yandex.ru/info?")
    suspend fun getUserInfo(
        //добавить Bearer перед токеном
        @Header("Authorization") token: String
    ): Response<User>
}