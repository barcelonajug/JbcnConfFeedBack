package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = Speaker.TABLE_NAME)
data class Speaker(
        @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(columnName = NAME_FIELD_NAME, dataType = DataType.STRING, canBeNull = false, unique = true)
        var name: String = "",

//        @DatabaseField(columnName = DESCRIPTION_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
//        var description: String = "",

//        @DatabaseField(columnName = BIOGRAPHY_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
//        var biogaphy: String = "",

        @DatabaseField(columnName = IMAGE_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
        var image: String = "",

//        @DatabaseField(columnName = URL_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
//        var url: String = "",

        @DatabaseField(columnName = REF_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var ref: String = "",

//        @DatabaseField(columnName = TWITTER_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
//        var twitter: String = "",

        @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = TALK_ID_FIELD_NAME)
        var talk: Talk? = null


//        @DatabaseField(columnName = HOMEPAGE_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
//        var homepage: String = "",
//
//        @DatabaseField(columnName = GENDER_FIELD_NAME, dataType = DataType.INTEGER, canBeNull = true)
//        var gender: Int = 0
) {

    companion object SpeakerData {
        const val TABLE_NAME = "speakers"
        const val ID_FIELD_NAME = "_id"
        const val NAME_FIELD_NAME = "name"
        const val DESCRIPTION_FIELD_NAME = "description"
        const val BIOGRAPHY_FIELD_NAME = "biography"
        const val IMAGE_FIELD_NAME = "image"
        const val URL_FIELD_NAME = "url"
        const val REF_FIELD_NAME = "ref"
        const val TWITTER_FIELD_NAME = "twitter"
        const val TALK_ID_FIELD_NAME = "talk_id"
        const val FOREIGN_FIELD = "talk"
//        const val HOMEPAGE_FIELD_NAME = "homepage"
//        const val GENDER_FIELD_NAME = "gender"
    }

}