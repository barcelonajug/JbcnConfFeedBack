package cat.cristina.pep.jbcnconffeedback.utils

import java.util.*


/*
* LocalTime not available until API 26, current is 23-24
*
* Use the method getTime() from Calendar to convert to a Date object
*
* */

/* Offset in minutes  */
private const val OFFSET = 15

enum class SeasonsTimes {

    MON_SE1 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 10, 30)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE - OFFSET))
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE + OFFSET))
            return calendar.time
        }
    };

    abstract fun getStartTime(): Calendar
    abstract fun getEndTime(): Calendar
    abstract fun getStartTimeMinusOffset(): Date
    abstract fun getEndTimePlusOffset(): Date

}

fun main(args: Array<String>) {
    val s: SeasonsTimes = SeasonsTimes.MON_SE1

}