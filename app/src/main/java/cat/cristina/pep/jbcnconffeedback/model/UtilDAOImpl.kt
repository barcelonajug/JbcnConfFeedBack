package cat.cristina.pep.jbcnconffeedback.model

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.stmt.QueryBuilder
import com.j256.ormlite.stmt.SelectArg
import com.j256.ormlite.stmt.Where


/** CRUD operations **/
data class UtilDAOImpl(val context: Context, val databaseHelper: DatabaseHelper) {

    fun lookupSpeakers(): List<Speaker> {
        val queryBuilder = databaseHelper.getSpeakerDao().queryBuilder()
        return queryBuilder.query()
    }

    fun lookupTalks(): List<Talk> {
        val queryBuilder = databaseHelper.getTalkDao().queryBuilder()
        return queryBuilder.query()
    }

    fun lookupSpeakersForTalk(talk: Talk) : List<Speaker> {
        var speakers: List<Speaker>? = null
        val joinQueryBuilder = databaseHelper.getSpeakerTalkDao().queryBuilder()
        joinQueryBuilder?.selectColumns(SpeakerTalk.SPEAKER_ID_FIELD_NAME)
        val userSelectArg = SelectArg()
        joinQueryBuilder?.where()!!.eq(SpeakerTalk.TALK_ID_FIELD_NAME, userSelectArg)

        val modulQueryBuilder = databaseHelper.getSpeakerDao().queryBuilder()
        modulQueryBuilder?.where()!!.`in`(Speaker.ID_FIELD_NAME, joinQueryBuilder)
        modulQueryBuilder.orderBy(Speaker.ID_FIELD_NAME, true)
        val preparedQuery = modulQueryBuilder!!.prepare()
        preparedQuery.setArgumentHolderValue(0, talk)
        speakers = databaseHelper.getSpeakerDao().query(preparedQuery)

        return speakers!!
    }

    fun lookupSpeakerByRef(ref: String) : Speaker {
        val qb: QueryBuilder<Speaker, Int> =  databaseHelper.getSpeakerDao().queryBuilder()
        val where: Where<Speaker, Int> = qb.where()
        val selectArg: SelectArg = SelectArg()

        qb.where().eq(Speaker.REF_FIELD_NAME, selectArg)
        val pq: PreparedQuery<Speaker> = qb.prepare()
        selectArg.setValue(ref)

        val speaker: Speaker = databaseHelper.getSpeakerDao().queryForFirst(pq)
        return speaker
    }

    fun onDestroy() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper()
            databaseHelper.close()
        }
    }

}