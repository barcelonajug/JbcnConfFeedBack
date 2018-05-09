package cat.cristina.pep.jbcnconffeedback.fragment.provider

import android.content.Context
import cat.cristina.pep.jbcnconffeedback.model.*
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import java.util.*

private val TAG = TalkContent::class.java.name

class TalkContent(val context: Context) {

    private var databaseHelper: DatabaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper::class.java)
    private var utilDAOImpl: UtilDAOImpl
    private var speakerDao: Dao<Speaker, Int>
    private var talkDao: Dao<Talk, Int>
    private var speakerTalkDao: Dao<SpeakerTalk, Int>

    /**
     * An array of talk items.
     */
    val ITEMS: MutableList<TalkItem> = ArrayList()

    /**
     * A map of talks by ID. Not used
     */
    val ITEM_MAP: MutableMap<String, TalkItem> = HashMap()

    init {
        utilDAOImpl = UtilDAOImpl(context, databaseHelper)
        talkDao = databaseHelper.getTalkDao()
        speakerDao = databaseHelper.getSpeakerDao()
        speakerTalkDao = databaseHelper.getSpeakerTalkDao()

        var i = 1
        talkDao
                .queryForAll()
                .forEach {
                    addItem(createTalkItem(i++, it))
                }

    }

    private fun addItem(item: TalkItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    /* TODO("description should be speaker") */
    private fun createTalkItem(position: Int, talk: Talk): TalkItem {
        val speakerRef: String = talk.speakers?.get(0) ?: "null"
        return TalkItem(position.toString(), talk, utilDAOImpl.lookupSpeakerByRef(speakerRef))
    }
    /* A talk item */
    data class TalkItem(val id: String, val talk: Talk, val speaker: Speaker)
}
