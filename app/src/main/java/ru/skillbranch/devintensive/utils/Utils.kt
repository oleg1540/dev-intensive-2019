package ru.skillbranch.devintensive.utils

import android.content.res.Resources
import kotlin.math.abs

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.split(" ")
        val firstName = parts?.getOrNull(0)?.trimIndent()
        val lastName = parts?.getOrNull(1)?.trimIndent()
        return (if (!firstName.isNullOrEmpty()) firstName else null) to (if (!lastName.isNullOrEmpty()) lastName else null)
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val map = mapOf<String, String>(
            "а" to "a",
            "б" to "b",
            "в" to "v",
            "г" to "g",
            "д" to "d",
            "е" to "e",
            "ё" to "e",
            "ж" to "zh",
            "з" to "z",
            "и" to "i",
            "й" to "i",
            "к" to "k",
            "л" to "l",
            "м" to "m",
            "н" to "n",
            "о" to "o",
            "п" to "p",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "у" to "u",
            "ф" to "f",
            "х" to "h",
            "ц" to "c",
            "ч" to "ch",
            "ш" to "sh",
            "щ" to "sh'",
            "ъ" to "",
            "ы" to "i",
            "ь" to "",
            "э" to "e",
            "ю" to "yu",
            "я" to "ya"
        )
        var result = ""
        var addString: String
        var isUpperCase: Boolean
        var key: String
        for (symbol in payload) {
            isUpperCase = symbol.isUpperCase()
            key = symbol.toLowerCase().toString()
            addString = when {
                key in map -> map[key].toString()
                key.compareTo(" ") === 0 -> divider
                else -> symbol.toString()
            }
            result = result.plus(if (isUpperCase) addString.capitalize() else addString)
        }
        return result
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var result: String? = null
        if (!firstName?.trim().isNullOrEmpty()) {
            result = firstName!![0].toUpperCase().toString()
        }
        if (!lastName?.trim().isNullOrEmpty()) {
            result = (if (result.isNullOrEmpty()) "" else result).plus(lastName!![0].toUpperCase().toString())
        }
        return result
    }

    fun plural(n: Int): Int {
        val n1 = abs(n) % 100
        val n2 = n1 % 10
        return when {
            n == 0 -> 0
            n2 == 1 -> 1
            n2 in 2..4 -> 2
            n1 in 11..19 -> 3
            else -> 3
        }
    }

    fun pluralizeSecond(n: Int, case: Int = 0): String {
        return when(plural(n)) {
            0 -> "секунд"
            1 -> if (case == 3) "секунду" else "секунда"
            2 -> "секунды"
            3 -> "секунд"
            else -> "секунд"
        }
    }

    fun pluralizeMinute(n: Int, case: Int = 0): String {
        return when(plural(n)) {
            0 -> "минут"
            1 -> if (case == 3) "минуту" else "минута"
            2 -> "минуты"
            3 -> "минут"
            else -> "минут"
        }
    }

    fun pluralizeHour(n: Int): String {
        return when(plural(n)) {
            0 -> "часов"
            1 -> "час"
            2 -> "часа"
            3 -> "часов"
            else -> "часов"
        }
    }

    fun pluralizeDay(n: Int): String {
        return when(plural(n)) {
            0 -> "дней"
            1 -> "день"
            2 -> "дня"
            3 -> "дней"
            else -> "дней"
        }
    }

    fun dpToPx(value: Int): Int = (value * Resources.getSystem().displayMetrics.density).toInt()

    fun pxToDp(value: Int): Int = (value / Resources.getSystem().displayMetrics.density).toInt()
}