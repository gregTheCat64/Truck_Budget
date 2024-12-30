package ru.javacat.data.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url
import ru.javacat.domain.models.DownloadUrl
import ru.javacat.domain.models.UploadUrlResponse
import ru.javacat.domain.models.User

interface ApiService {
    @GET("https://login.yandex.ru/info?")
    suspend fun getUserInfo(
        //добавить Bearer перед токеном
        @Header("Authorization") token: String
    ): Response<User>

    @GET("https://cloud-api.yandex.net/v1/disk/resources/upload")
    suspend fun getUploadUrl(
        @Header("Authorization") token: String,
        @Query("path") path: String,
        @Query("overwrite") overwrite: Boolean = true
    ): Response<UploadUrlResponse>

    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Body file: RequestBody
    ): Response<ResponseBody>

    @GET("https://cloud-api.yandex.net/v1/disk/resources/download")
    suspend fun getDownloadUrl(
        @Header("Authorization") token: String,
        @Query("path") path: String
    ): Response<DownloadUrl>

    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>
}