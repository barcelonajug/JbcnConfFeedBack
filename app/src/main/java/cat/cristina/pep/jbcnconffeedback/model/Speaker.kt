package cat.cristina.pep.jbcnconffeedback.model

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "speakers")
data class Speaker(
        @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME, dataType = DataType.INTEGER)
        val id: Int = 0,

        @DatabaseField(columnName = ENABLED_FIELD_NAME, dataType = DataType.INTEGER, canBeNull = false)
        var isEnabled: Int = 0,

        @DatabaseField(columnName = NAME_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var name: String? = null,

        @DatabaseField(columnName = DESCRIPTION_FIELD_NAME, dataType = DataType.STRING, canBeNull = true)
        var description: String? = null,

        @DatabaseField(columnName = BIOGRAPHY_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var biogaphy: String? = null,

        @DatabaseField(columnName = IMAGE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var image: String? = null,

        @DatabaseField(columnName = URL_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var url: String? = null,

        @DatabaseField(columnName = REF_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var ref: String? = null,

        @DatabaseField(columnName = TWITTER_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var twitter: String? = null,

        @DatabaseField(columnName = HOMEPAGE_FIELD_NAME, dataType = DataType.STRING, canBeNull = false)
        var homepage: String? = null,

        @DatabaseField(columnName = GENDER_FIELD_NAME, dataType = DataType.INTEGER, canBeNull = true)
        var gender: Int = 0
) {

    companion object {
        const val ID_FIELD_NAME = "_id"
        const val ENABLED_FIELD_NAME = "enabled"
        const val NAME_FIELD_NAME = "name"
        const val DESCRIPTION_FIELD_NAME = "description"
        const val BIOGRAPHY_FIELD_NAME = "biography"
        const val IMAGE_FIELD_NAME = "image"
        const val URL_FIELD_NAME = "url"
        const val REF_FIELD_NAME = "ref"
        const val TWITTER_FIELD_NAME = "twitter"
        const val HOMEPAGE_FIELD_NAME = "homepage"
        const val GENDER_FIELD_NAME = "gender"
    }

}