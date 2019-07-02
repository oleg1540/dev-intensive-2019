package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User (
    val id: String,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var rating: Int = 0,
    var respect: Int = 0,
    var lastVisit: Date? = Date(),
    var isOnline: Boolean = false
) {
    constructor(id: String, firstName: String?, lastName: String?) : this(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatar = null
    )

    constructor(id: String) : this(id, "John", "Doe $id")

    init {
        println("It's alive!!!\n" +
        "${if (lastName === "Doe") "His name is $firstName $lastName" else "And his name is $firstName $lastName!!!"}\n")
    }

    fun printMe() = println("""
            id: $id
            firstName: $firstName
            lastName: $lastName
            avatar: $avatar
            rating: $rating
            respect: $respect
            lastVisit:$lastVisit
            isOnline: $isOnline
        """)

    companion object Factory {
        private var _lastId: Int = -1
        fun makeUser(fullName: String?): User {
            _lastId++;
            val (firstName, lastName) = Utils.parseFullName(fullName)
            return User(id = "$_lastId", firstName = firstName, lastName = lastName)
        }
    }

    class Builder {
        private var id: String = ""
        private var firstName: String = ""
        private var lastName: String = ""
        private var avatar: String = ""
        private var rating: Int = 0
        private var respect: Int = 0
        private var lastVisit: Date = Date()
        private var isOnline: Boolean = false

        fun id(value: String)  {
            id = value
        }

        fun firstName(value: String) {
            firstName = value
        }

        fun lastName(value: String) {
            lastName = value
        }

        fun avatar(value: String) {
            avatar = value
        }

        fun rating(value: Int) {
            rating = value
        }

        fun respect(value: Int) {
            respect = value
        }

        fun lastVisit(value: Date) {
            lastVisit = value
        }

        fun isOnline(value: Boolean) {
            isOnline = value
        }

        fun build(): User {
            return User(id, firstName, lastName, avatar, rating, respect, lastVisit, isOnline)
        }
    }
}