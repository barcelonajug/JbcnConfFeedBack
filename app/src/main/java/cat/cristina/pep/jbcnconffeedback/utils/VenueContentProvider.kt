package cat.cristina.pep.jbcnconffeedback.utils

import android.content.Context
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.Charset

class VenueContentProvider(val context: Context, val fileName: String) {

    var venuesMap: MutableMap<String, String> = mutableMapOf()

    private data class Venue(val id: String, val room: String)

    init {
        processData(readData(fileName))
    }

    /*
    * id has format: MO
    *
    * */
    fun getRoom(id: String): String {
        return venuesMap[id]!!
    }

    private fun readData(fileName: String): String {

        val inputStream = context.assets.open(fileName)
        val byteArray = ByteArray(inputStream.available())
        inputStream.read(byteArray)
        inputStream.close()
        return String(byteArray, Charset.defaultCharset())

    }

    /*
    * id has format: MON-TC1
    *
    * */
    private fun processData(jsonOfSchedules: String): Unit {

        val jsonObject = JSONObject(jsonOfSchedules)
        val schedules = jsonObject.getJSONArray(VENUES_PROVIDER_JSON_COLLECTION_NAME)
        val gson = Gson()

        for (index in 0 until (schedules.length())) {
            val scheduleObject = schedules.getJSONObject(index)
            val venue: Venue = gson.fromJson(scheduleObject.toString(), Venue::class.java)
            venuesMap[venue.id] = venue.room
        }
    }

    companion object {
        const val VENUES_PROVIDER_JSON_COLLECTION_NAME = "venues"
    }
}