package cat.cristina.pep.jbcnconffeedback.utils

import java.util.*


/* LocalTime not available until API 26, current is 23-24 */
enum class SeasonsTimes {

    MON_SE1 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    MON_SE2 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    MON_SE3 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    MON_SE4 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    MON_SE5 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    MON_SE6 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    MON_SE7 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE1 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE2 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE3 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE4 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE5 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE6 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    TUE_SE7 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE1 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE2 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE3 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE4 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE5 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE6 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    },
    WED_SE7 {
        override fun getStartTime() = GregorianCalendar(2018, 6, 11, 9, 40)
        override fun getEndTime() = GregorianCalendar(2018, 6, 11, 10, 30)
    };

    abstract fun getStartTime(): Calendar
    abstract fun getEndTime(): Calendar

}

fun main(args: Array<String>) {
    val s: SeasonsTimes = SeasonsTimes.MON_SE1


}