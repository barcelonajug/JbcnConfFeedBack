package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "talks")
data class Talk (
        @DatabaseField(generatedId = true, columnName = Speaker.ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(columnName = TITLE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var title: String? = null,

        @DatabaseField(columnName = ABSTRACT_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var description: String? = null,

        @DatabaseField(columnName = TYPE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var type: String? = null,

        @DatabaseField(columnName = TAGS_FIELD_NAME, dataType = DataType.SERIALIZABLE, canBeNull = false)
        var tags: Array<String>? = null,

        @DatabaseField(columnName = LEVEL_FIELD_NAME, dataType = DataType.INTEGER, canBeNull = false)
        var level: Int = 0
) {

    companion object {
        const val ID_FIELD_NAME = "_id"
        const val TITLE_FIELD_NAME = "title"
        const val ABSTRACT_FIELD_NAME = "abstract"
        const val TYPE_FIELD_NAME = "type"
        const val TAGS_FIELD_NAME = "tags"
        const val LEVEL_FIELD_NAME = "level"
    }
}