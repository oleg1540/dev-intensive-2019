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

fun String.isGithubUrl(): Boolean {
    val regex = Regex(pattern = "^(https:\\/\\/|www\\.|https:\\/\\/www\\.)*github\\.com\\/((?!(login|enterprise|features|topics|collections|trending|events|marketplace|pricing|nonprofit|customer-stories|security|join)\$)[a-zA-Z\\d-]+)\\/?\$")
    return this.isEmpty() || regex.containsMatchIn(input = this.trim())    // matched: true
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