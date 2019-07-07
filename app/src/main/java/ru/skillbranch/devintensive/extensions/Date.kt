package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.utils.Utils
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

enum class TimeUnits {
    SECOND {
        override fun plural(i: Int): String {
            return prefixPlural(i) + Utils.pluralizeSecond(i)
        }
    },
    MINUTE {
        override fun plural(i: Int): String {
            return prefixPlural(i) + Utils.pluralizeMinute(i)
        }
    },
    HOUR {
        override fun plural(i: Int): String {
            return prefixPlural(i) + Utils.pluralizeHour(i)
        }
    },
    DAY {
        override fun plural(i: Int): String {
            return prefixPlural(i) + Utils.pluralizeDay(i)
        }
    };

    abstract fun plural(i: Int): String
    fun prefixPlural(i:Int): String {
        return "$i "
    }
}

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
        else -> throw IllegalStateException("Invalid input")
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(): String {
    val diff = Date().time - this.time
    return when {
        0 <= diff && diff <= 1 * SECOND -> "только что"
        1 * SECOND < diff && diff <= 45 * SECOND -> "несколько секунд назад"
        45 * SECOND < diff && diff <= 75 * SECOND -> "минуту назад"
        75 * SECOND < diff && diff <= 45 * MINUTE -> "${diff / MINUTE} ${Utils.pluralizeMinute((diff / MINUTE).toInt())} назад"
        45 * MINUTE < diff && diff <= 75 * MINUTE -> "час назад"
        75 * MINUTE < diff && diff <= 22 * HOUR -> "${diff / HOUR} ${Utils.pluralizeHour((diff / HOUR).toInt())} назад"
        22 * HOUR < diff && diff <= 26 * HOUR -> "день назад"
        26 * HOUR < diff && diff <= 360 * DAY -> "${diff / DAY} ${Utils.pluralizeDay((diff / DAY).toInt())} назад"
        360 * DAY < diff -> "более года назад"

        -1 * SECOND <= diff && diff < 0  -> "только что"
        -45 * SECOND <= diff && diff < -1 * SECOND -> "через несколько секунд"
        -45 * SECOND <= diff && diff < -75 * SECOND -> "через минуту"
        -45 * MINUTE <= diff && diff < -75 * SECOND -> "через ${abs(diff / MINUTE)} ${Utils.pluralizeMinute(abs(diff / MINUTE).toInt())}"
        -75 * MINUTE <= diff && diff < -45 * MINUTE -> "через час"
        -22 * HOUR <= diff && diff < -75 * MINUTE -> "через ${abs(diff / HOUR)} ${Utils.pluralizeHour(abs(diff / HOUR).toInt())}"
        -26 * HOUR <= diff && diff < -22 * HOUR -> "через день"
        -360 * DAY <= diff && diff < -26 * HOUR -> "через ${abs(diff / DAY)} ${Utils.pluralizeDay(abs(diff / DAY).toInt())}"
        diff < -360 * DAY -> "более чем через год"

        else -> "неизвестно"
    }
}