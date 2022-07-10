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
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 9, 15)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 9, 55)
    },

    MON_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 10, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 10, 35)
    },

    MON_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 11, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 12, 0)
    },

    MON_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 12, 15)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 13, 5)
    },

    MON_SE5 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 14, 30)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 15, 20)
    },

    MON_SE6 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 15, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 16, 25)
    },

    MON_SE7 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 17, 5)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 17, 55)
    },
    MON_SE8 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 18, 18, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 18, 19, 0)
    },

    TUE_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 9, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 9, 50)
    },

    TUE_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 10, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 10, 50)
    },

    TUE_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 11, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 12, 0)
    },

    TUE_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 12, 15)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 13, 5)
    },

    TUE_SE5 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 14, 30)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 15, 20)
    },

    TUE_SE6 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 15, 25)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 16, 15)
    },

    TUE_SE7 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 16, 55)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 17, 45)
    },
    TUE_SE8 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 19, 18, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 18, 40)
    },
    TUE_SE9 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(  2022, Calendar.JULY, 19, 18, 45)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 19, 19, 0)
    },

    WED_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 20, 9, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 20, 10, 55)
    },

    WED_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2022, Calendar.JULY, 20, 11, 40)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2022, Calendar.JULY, 20, 13, 35)
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
