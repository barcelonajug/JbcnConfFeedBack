package cat.cristina.pep.jbcnconffeedback.utils

import android.content.Context
import org.json.JSONObject
import java.nio.charset.Charset

class ReadData public constructor(val context: Context) {

    private fun readData(fileName: String): String {

        val inputStream = context.assets.open(fileName)
        val byteArray = ByteArray(inputStream.available())
        inputStream.read(byteArray)
        inputStream.close()
        return String(byteArray, Charset.defaultCharset())

    }

    private fun processData(jsonFileName: String): Unit {

        val jsonObject = JSONObject(jsonFileName)


    }
}