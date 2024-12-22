package ru.javacat.data.impl

import android.util.Log
import ru.javacat.data.apiRequest
import ru.javacat.data.network.ApiService
import ru.javacat.domain.models.User
import ru.javacat.domain.repo.ApiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
): ApiRepository {
    private val TAG = "ApiRepoImpl"

    override suspend fun getUserInfo(token: String): User {
        val result = apiRequest {
            apiService.getUserInfo("Bearer $token")
        }
        Log.i(TAG, "user is gotten. It is: $result")
        return result
    }

    override suspend fun getUserPic(picId: String) {
        TODO("Not yet implemented")
    }
}