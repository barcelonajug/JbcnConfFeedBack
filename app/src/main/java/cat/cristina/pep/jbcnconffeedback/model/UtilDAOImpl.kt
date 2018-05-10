package cat.cristina.pep.jbcnconffeedback.model

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.stmt.QueryBuilder
import com.j256.ormlite.stmt.SelectArg


private val TAG: String = UtilDAOImpl::class.java.name

/** CRUD operations **/
data class UtilDAOImpl(val context: Context, private val databaseHelper: DatabaseHelper) {

    fun lookupSpeakers(): List<Speaker> = databaseHelper.getSpeakerDao().queryBuilder().query()

    fun lookupTalks(): List<Talk> = databaseHelper.getTalkDao().queryBuilder().query()

    fun lookupSpeakersForTalk(talk: Talk): List<Speaker> {

        val joinQueryBuilder: QueryBuilder<SpeakerTalk, Int> = databaseHelper
                .getSpeakerTalkDao().queryBuilder()
        joinQueryBuilder.selectColumns(SpeakerTalk.SPEAKER_ID_FIELD_NAME)
        joinQueryBuilder.where().eq(SpeakerTalk.TALK_ID_FIELD_NAME, SelectArg())

        val moduleQueryBuilder: QueryBuilder<Speaker, Int> = databaseHelper
                .getSpeakerDao().queryBuilder()
        moduleQueryBuilder.where().`in`(Speaker.ID_FIELD_NAME, joinQueryBuilder)
        moduleQueryBuilder.orderBy(Speaker.ID_FIELD_NAME, true)
        val preparedQuery: PreparedQuery<Speaker> = moduleQueryBuilder.prepare()
        preparedQuery.setArgumentHolderValue(0, talk)

        return databaseHelper.getSpeakerDao().query(preparedQuery)
    }

    /*
    * Q2hyaXN0aWFuUG9zdGFjaHJpc3RpYW4ucG9zdGFAZ21haWwuY29t
    *
    * Q2hyaXN0aWFuUG9zdGFjaHJpc3RpYW4ucG9zdGFAZ21haWwuY29t
    *
    * */
    fun lookupSpeakerByRef(ref: String): Speaker {
        val qb: QueryBuilder<Speaker, Int> = databaseHelper.getSpeakerDao().queryBuilder()
        // val where: Where<Speaker, Int> = qb.where()
        val selectArg: SelectArg = SelectArg()

        qb.where().eq(Speaker.REF_FIELD_NAME, selectArg)
        val pq: PreparedQuery<Speaker> = qb.prepare()
        selectArg.setValue(ref)

        return databaseHelper.getSpeakerDao().queryForFirst(pq)
    }

    fun onDestroy() {
        OpenHelperManager.releaseHelper()
        databaseHelper.close()
    }

}