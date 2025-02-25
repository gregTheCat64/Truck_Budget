package ru.javacat.domain.models

data class User(
    val birthday: String,
    val client_id: String,
    val default_avatar_id: String,
    val default_email: String,
    val default_phone: DefaultPhone,
    val display_name: String,
    val emails: List<String>,
    val first_name: String,
    val id: String,
    val is_avatar_empty: Boolean,
    val last_name: String,
    val login: String,
    val psuid: String,
    val real_name: String,
    val sex: String
)

class Person {
    var name: String
    var age: Int

    init {
        println("Initializing the object")
    }

    constructor(name:String) {
        this.name = name
        this.age = 0
    }

    constructor(age: Int): this("Unknown"){
        this.age = age
    }

    constructor(name: String, age: Int): this(name){
        this.age = age
    }
}