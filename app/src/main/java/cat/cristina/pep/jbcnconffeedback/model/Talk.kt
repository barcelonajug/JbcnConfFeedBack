package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = Talk.TABLE_NAME)
data class Talk(
        @DatabaseField(columnName = ID_FIELD_NAME, dataType = DataType.STRING)
        val id: String = "",

        @DatabaseField(generatedId = true, columnName = SQL_ID_NAME, dataType = DataType.INTEGER)
        val oid: Int = 0,

        @DatabaseField(columnName = TITLE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false, unique = true)
        var title: String = "",

        @DatabaseField(columnName = ABSTRACT_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
        var description: String = "",

        @DatabaseField(columnName = TYPE_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
        var type: String = "",

//        @DatabaseField(columnName = TAGS_FIELD_NAME, dataType = DataType.SERIALIZABLE, canBeNull = false)
//        var tags: Array<String>? = null,

        @DatabaseField(columnName = LEVEL_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
        var level: String = "",

        @DatabaseField(columnName = SCHEDULEDID_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
        var scheduleId: String = "",

        @DatabaseField(columnName = SPEAKERS_FIELD_NAME, dataType = DataType.SERIALIZABLE, canBeNull = false)
        var speakers: ArrayList<String> = ArrayList<String>()) : Comparable<Talk> {



    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Talk): Int =
            this.scheduleId.compareTo(other.scheduleId)

    companion object TalkData {
        const val TABLE_NAME = "talks"
        const val ID_FIELD_NAME = "_id"
        const val SQL_ID_NAME = "oid"
        const val TITLE_FIELD_NAME = "title"
        const val ABSTRACT_FIELD_NAME = "abstract"
        const val TYPE_FIELD_NAME = "type"
        //const val TAGS_FIELD_NAME = "tags"
        const val LEVEL_FIELD_NAME = "level"
        const val SCHEDULEDID_FIELD_NAME = "scheduleId"
        const val SPEAKERS_FIELD_NAME = "speakers"
    }
}