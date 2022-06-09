package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = Score.TABLE_NAME)
data class Score constructor (
        @DatabaseField(generatedId = true, columnName = FIELD_NAME_ID, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(columnName = FIELD_NAME_TALK_ID, dataType = DataType.STRING, canBeNull = false)
        val talk_id: String = "",

        @DatabaseField(columnName = FIELD_NAME_SCHEDULE_ID, dataType = DataType.STRING, canBeNull = false)
        val schedule_id: String = "",

        @DatabaseField(columnName = FIELD_NAME_SCORE, dataType = DataType.INTEGER, canBeNull = false)
        var score: Int = 0,

        @DatabaseField(columnName = FIELD_NAME_DATE, dataType = DataType.DATE, canBeNull = false)
        var date: Date = Date()) {

    companion object ScoreData {
        const val TABLE_NAME = "scores"
        const val FIELD_NAME_ID = "_id"
        const val FIELD_NAME_TALK_ID = "talk_id"
        const val FIELD_NAME_SCHEDULE_ID = "schedule_id"
        const val FIELD_NAME_SCORE = "score"
        const val FIELD_NAME_DATE = "date"
    }
}