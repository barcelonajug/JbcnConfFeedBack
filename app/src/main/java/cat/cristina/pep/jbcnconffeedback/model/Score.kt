package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = Score.TABLE_NAME)
data class Score constructor (
        @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(columnName = SCORE_FIELD_NAME, dataType = DataType.INTEGER, canBeNull = false)
        var score: Int = 0

) {

    companion object ScoreData {
        const val TABLE_NAME = "scores"
        const val ID_FIELD_NAME = "_id"
        const val SCORE_FIELD_NAME = "score"
    }
}