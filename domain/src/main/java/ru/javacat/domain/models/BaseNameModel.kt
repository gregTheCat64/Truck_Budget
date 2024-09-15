package ru.javacat.domain.models

abstract class BaseModel

abstract class BaseIdModel<T: Any?>: BaseModel() {
    abstract val id: T?
}

abstract class BaseNameModel<T: Any?>: BaseIdModel<T>(){
    abstract val positionId: Long
    abstract val nameToShow: String
    abstract val isHidden: Boolean
}

abstract class BaseNameLongIdModel: BaseNameModel<Long?>()

abstract class BaseNameStringIdModel: BaseNameModel<String>()