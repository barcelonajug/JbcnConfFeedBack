package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "speakertalk")
data class SpeakerTalk (
        @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(foreign = true, columnName = SPEAKER_ID_FIELD_NAME)
        val speaker: Speaker? = null,

        @DatabaseField(foreign = true, columnName = TALK_ID_FIELD_NAME)
        val talk: Talk? = null
) {

    companion object {
        const val ID_FIELD_NAME = "_id"
        const val SPEAKER_ID_FIELD_NAME = "speaker_id"
        const val TALK_ID_FIELD_NAME = "talk_id"
    }
}