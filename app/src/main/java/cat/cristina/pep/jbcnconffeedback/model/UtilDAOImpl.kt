package cat.cristina.pep.jbcnconffeedback.model

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao

/** CRUD operations **/
data class UtilDAOImpl(val context: Context) {
    private var databaseHelper: DatabaseHelper? = null
    private var speakerDao: Dao<Speaker, Int>? = null
    private var talkDao: Dao<Talk, Int>? = null

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

    private fun getSetup() {
        databaseHelper = helper
        this.speakerDao = databaseHelper!!.getSpeakerDao()
        this.talkDao = databaseHelper!!.getTalkDao()
    }

    fun onDestroy() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper()
            databaseHelper = null
        }
    }

}