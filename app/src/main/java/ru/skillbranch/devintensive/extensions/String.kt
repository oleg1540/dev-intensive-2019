package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16): String {
    val suffix = "..."
    var result = this.trim()
    if (result.length > length) {
        result = result.substring(0, length).trim().plus(suffix)
    }
    return result
}

fun String.stripHtml(): String {
    return this.replace("<[^>]*>".toRegex(),"").trim().removeHtmlEscape().removeDoubleWhitespace()
}

private fun String.removeDoubleWhitespace(): String {
    var result = ""
    var prev: Char = 0.toChar()
    for (word in this) {
        if (!word.isWhitespace() || prev.compareTo(word) != 0) {
            result = result.plus(word)
        }
        prev = word
    }
    return result
}

private fun String.removeHtmlEscape(): String {
    return this.replace("[&<>'\"]*".toRegex(), "")
}