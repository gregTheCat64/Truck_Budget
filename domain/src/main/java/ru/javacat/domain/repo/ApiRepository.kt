package ru.javacat.domain.repo

import ru.javacat.domain.models.User

interface ApiRepository {

    suspend fun getUserInfo(token: String): User

    suspend fun getUserPic(picId: String)

}