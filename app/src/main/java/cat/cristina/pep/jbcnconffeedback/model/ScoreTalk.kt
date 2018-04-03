package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "scoretalk")
data class ScoreTalk (
        @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(foreign = true, columnName = SCORE_ID_FIELD_NAME)
        val score: Score,

        @DatabaseField(foreign = true, columnName = TALK_ID_FIELD_NAME)
        val talk: Talk

){

    companion object {
        const val ID_FIELD_NAME = "_id"
        const val SCORE_ID_FIELD_NAME = "score_id"
        const val TALK_ID_FIELD_NAME = "talk_id"
    }

}