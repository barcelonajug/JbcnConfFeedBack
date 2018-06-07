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

enum class SessionsTimes {

    MON_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 11, 9, 40)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 11, 10, 30)
    },

    MON_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 11, 11, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 11, 12, 0)
    },

    MON_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 11, 12, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 11, 13, 0)
    },

    MON_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 11, 14, 30)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 11, 15, 20)
    },

    MON_SE5 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 11, 15, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 11, 16, 25)
    },

    MON_SE6 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 11, 17, 5)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 11, 17, 55)
    },

    TUE_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 12, 9, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 12, 9, 50)
    },

    TUE_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 12, 10, 5)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 12, 10, 55)
    },

    TUE_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 12, 11, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 12, 12, 25)
    },

    TUE_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 12, 12, 40)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 12, 13, 30)
    },

    TUE_SE5 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 12, 14, 55)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 12, 15, 45)
    },

    TUE_SE6 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 12, 16, 25)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 12, 17, 15)
    },

    WED_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 13, 9, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 13, 10, 55)
    },

    WED_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 13, 11, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 13, 13, 30)
    },

    WED_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 13, 14,45)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 13, 16, 40)
    },

    WED_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2018, Calendar.JUNE, 13, 17, 20)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2018, Calendar.JUNE, 13, 19, 15)
    };

    abstract fun getStartTalkDateTime(): Calendar
    abstract fun getEndTalkDateTime(): Calendar

    fun getStartScheduleDateTime(): Calendar {
        val calendar = getEndTalkDateTime()
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
        return calendar
    }

    fun getEndScheduleDateTime(): Calendar {
        val calendar = getEndTalkDateTime()
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
        return calendar
    }

}
