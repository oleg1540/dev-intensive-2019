package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16): String {
    val suffix = "..."
    var result = this.trim()
    if (result.length > length) {
        result = result.substring(0, length).trim().plus(suffix)
    }
    return result
}