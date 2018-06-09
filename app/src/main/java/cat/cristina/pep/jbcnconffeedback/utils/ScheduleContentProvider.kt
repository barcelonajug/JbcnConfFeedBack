package cat.cristina.pep.jbcnconffeedback.utils

import android.content.Context
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

private const val OFFSET = 15

class ScheduleContentProvider(val context: Context, val fileName: String) {

    var scheduleMap: MutableMap<String, Pair<Calendar, Calendar>> = mutableMapOf()

    private data class Schedule(val id: String, val start: String, val end: String)

    init {
        processData(readData(fileName))
    }

    /*
    * scheduleId format is 'MON-SE1'
    * */
    public fun getStartTalkDateTime(scheduleId: String): Calendar {
        return scheduleMap[scheduleId]!!.first
    }

    public fun getEndTalkDateTime(scheduleId: String): Calendar {
        return scheduleMap[scheduleId]!!.second
    }

    public fun getStartScheduleDateTime(scheduleId: String): Calendar {
        val calendar = getEndTalkDateTime(scheduleId)
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - OFFSET)
        return calendar
    }

    public fun getEndScheduleDateTime(scheduleId: String): Calendar {
        val calendar = getEndTalkDateTime(scheduleId)
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + OFFSET)
        return calendar
    }


    private fun readData(fileName: String): String {

        val inputStream = context.assets.open(fileName)
        val byteArray = ByteArray(inputStream.available())
        inputStream.read(byteArray)
        inputStream.close()
        return String(byteArray, Charset.defaultCharset())

    }

    private fun processData(jsonOfSchedules: String): Unit {

        val jsonObject = JSONObject(jsonOfSchedules)
        val schedules = jsonObject.getJSONArray(SCHEDULE_PROVIDER_JSON_COLLECTION_NAME)
        val gson = Gson()

        for (index in 0 until (schedules.length())) {
            val scheduleObject = schedules.getJSONObject(index)
            val schedule: Schedule = gson.fromJson(scheduleObject.toString(), Schedule::class.java)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val start = simpleDateFormat.parse(schedule.start)
            val calendarStart = GregorianCalendar()
            calendarStart.time = start
            val end = simpleDateFormat.parse(schedule.end)
            val calendarEnd = GregorianCalendar()
            calendarEnd.time = end
            scheduleMap[schedule.id] = calendarStart to calendarEnd
        }
    }

    companion object {
        const val SCHEDULE_PROVIDER_JSON_COLLECTION_NAME = "schedules"
    }
}