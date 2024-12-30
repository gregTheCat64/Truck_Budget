package ru.javacat.domain.models

data class UploadUrlResponse(
    val href: String,
    val method: String,
    val templated: Boolean
)

data class DownloadUrl(val href: String)