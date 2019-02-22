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
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 27, 9, 40)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 27, 10, 30)
    },

    MON_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 27, 11, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 27, 12, 0)
    },

    MON_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 27, 12, 10)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 27, 13, 0)
    },

    MON_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 27, 14, 30)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 27, 15, 20)
    },

    MON_SE5 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 27, 15, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 27, 16, 25)
    },

    MON_SE6 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 27, 17, 5)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 27, 17, 55)
    },

    TUE_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 28, 9, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 28, 9, 50)
    },

    TUE_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 28, 10, 5)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 28, 10, 55)
    },

    TUE_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 28, 11, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 28, 12, 25)
    },

    TUE_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 28, 12, 40)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 28, 13, 30)
    },

    TUE_SE5 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 28, 14, 55)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 28, 15, 45)
    },

    TUE_SE6 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 28, 16, 25)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 28, 17, 15)
    },

    WED_SE1 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 29, 9, 0)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 29, 10, 55)
    },

    WED_SE2 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 29, 11, 35)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 29, 13, 30)
    },

    WED_SE3 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 29, 14,45)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 29, 16, 40)
    },

    WED_SE4 {
        override fun getStartTalkDateTime(): Calendar = GregorianCalendar(2019, Calendar.MAY, 29, 17, 20)
        override fun getEndTalkDateTime(): Calendar  = GregorianCalendar(2019, Calendar.MAY, 29, 19, 15)
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
