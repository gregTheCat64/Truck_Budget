package ru.javacat.domain.models

abstract class BaseNameModel{
    abstract val id: Long?
    abstract val positionId: Long?
    abstract val name: String
}