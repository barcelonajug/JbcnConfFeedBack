package cat.cristina.pep.jbcnconffeedback.utils

import java.util.*

data class SessionTimes(
        val startTalkDateTime: Calendar,
        val endTalkDateTime: Calendar,
        val offsetInMinutes: Int = 15) {

    var startScheduleDdateTime: Calendar
    var endScheduleDdateTime: Calendar

    init {
        startScheduleDdateTime = endTalkDateTime
        startScheduleDdateTime.set(Calendar.MINUTE, startScheduleDdateTime.get(Calendar.MINUTE) - offsetInMinutes)
        endScheduleDdateTime = endTalkDateTime
        endScheduleDdateTime.set(Calendar.MINUTE, endScheduleDdateTime.get(Calendar.MINUTE) + offsetInMinutes)
    }
}
