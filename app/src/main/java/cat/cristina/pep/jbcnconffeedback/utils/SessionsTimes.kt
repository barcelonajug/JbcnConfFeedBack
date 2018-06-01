package cat.cristina.pep.jbcnconffeedback.utils

import java.util.*


/*
* LocalTime not available until API 26, current is 23-24
*
* Use the method getTime() from Calendar to convert to a Date object
*
* */

/* Offset in minutes  */
private const val OFFSET = 1

enum class SessionsTimes {

    MON_SE1 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 0)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 3)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    MON_SE2 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 5)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 8)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    MON_SE3 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 10)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 13)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    MON_SE4 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 15)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 18)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    MON_SE5 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 20)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 23)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    MON_SE6 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 25)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 28)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    MON_SE7 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 11, 9, 30)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 11, 9, 33)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE1 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 9, 0)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 9, 50)
        override fun getStartTimeMinusOffset(): Date {
            val calendar =  getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE2 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 10, 0)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 10, 50)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE3 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 11, 30)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 12, 20)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE4 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 12, 30)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 13, 20)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE5 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 14, 35)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 15, 25)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE6 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 15, 35)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 16, 25)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE7 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 17, 5)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 17, 55)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    TUE_SE8 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 12, 18, 0)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 12, 18, 30)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    WED_SE1 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 13, 9, 0)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 13, 11, 0)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    },

    WED_SE2 {
        override fun getStartTime(): Calendar = GregorianCalendar(2018, 6, 13, 11, 30)
        override fun getEndTime(): Calendar  = GregorianCalendar(2018, 6, 13, 13, 30)
        override fun getStartTimeMinusOffset(): Date {
            val calendar = getStartTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
            return calendar.time
        }
        override fun getEndTimePlusOffset(): Date {
            val calendar = getEndTime()
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
            return calendar.time
        }
    };

    abstract fun getStartTime(): Calendar
    abstract fun getEndTime(): Calendar
    abstract fun getStartTimeMinusOffset(): Date
    abstract fun getEndTimePlusOffset(): Date

}
