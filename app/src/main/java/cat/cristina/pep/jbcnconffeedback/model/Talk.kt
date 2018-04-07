package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "talks")
data class Talk (
        @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(columnName = TITLE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var title: String = "",

        @DatabaseField(columnName = ABSTRACT_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var description: String = "",

        @DatabaseField(columnName = TYPE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var type: String = "",

//        @DatabaseField(columnName = TAGS_FIELD_NAME, dataType = DataType.SERIALIZABLE, canBeNull = false)
//        var tags: Array<String>? = null,

        @DatabaseField(columnName = LEVEL_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var level: String = "",

        @DatabaseField(columnName = SPEAKERS_FIELD_NAME, dataType = DataType.SERIALIZABLE, canBeNull = false)
        var speakers: Array<String>? = null
) {

    companion object {
        const val ID_FIELD_NAME = "_id"
        const val TITLE_FIELD_NAME = "title"
        const val ABSTRACT_FIELD_NAME = "abstract"
        const val TYPE_FIELD_NAME = "type"
        //const val TAGS_FIELD_NAME = "tags"
        const val LEVEL_FIELD_NAME = "level"
        const val SPEAKERS_FIELD_NAME = "speakers"
    }
}