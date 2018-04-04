package cat.cristina.pep.jbcnconffeedback.model

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.stmt.SelectArg



/** CRUD operations **/
data class UtilDAOImpl(val context: Context) {
    var databaseHelper: DatabaseHelper? = null
    private var speakerDao: Dao<Speaker, Int>? = null
    private var talkDao: Dao<Talk, Int>? = null
    private var speakerTalkDao: Dao<SpeakerTalk, Int>? = null

    private val helper: DatabaseHelper
        get() {
            if (databaseHelper == null) {
                databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper::class.java)
            }
            return databaseHelper!!
        }

    init {
        getSetup()
    }

    fun lookupSpeakers(): List<Speaker> {
        val queryBuilder = speakerDao!!.queryBuilder()
        return queryBuilder.query()
    }

    fun lookupTalks(): List<Talk> {
        val queryBuilder = talkDao!!.queryBuilder()
        return queryBuilder.query()
    }

    fun lookupSpeakersForTalk(talk: Talk) : List<Speaker> {
        var speakers: List<Speaker>? = null
        val joinQueryBuilder = speakerTalkDao?.queryBuilder()
        joinQueryBuilder?.selectColumns(SpeakerTalk.SPEAKER_ID_FIELD_NAME)
        val userSelectArg = SelectArg()
        joinQueryBuilder?.where()!!.eq(SpeakerTalk.TALK_ID_FIELD_NAME, userSelectArg)

        val modulQueryBuilder = speakerDao?.queryBuilder()
        modulQueryBuilder?.where()!!.`in`(Speaker.ID_FIELD_NAME, joinQueryBuilder)
        modulQueryBuilder.orderBy(Speaker.ID_FIELD_NAME, true)
        val preparedQuery = modulQueryBuilder!!.prepare()
        preparedQuery.setArgumentHolderValue(0, talk)
        speakers = speakerDao?.query(preparedQuery)

        return speakers!!
    }

    private fun getSetup() {
        databaseHelper = helper
        this.speakerDao = databaseHelper!!.getSpeakerDao()
        this.talkDao = databaseHelper!!.getTalkDao()
        this.speakerTalkDao = databaseHelper!!.getSpeakerTalkDao()
    }

    fun onDestroy() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper()
            databaseHelper = null
        }
    }

}