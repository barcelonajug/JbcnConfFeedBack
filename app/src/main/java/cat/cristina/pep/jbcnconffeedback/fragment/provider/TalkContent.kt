package cat.cristina.pep.jbcnconffeedback.fragment.provider

import android.content.Context
import android.support.v7.preference.PreferenceManager
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.model.*
import cat.cristina.pep.jbcnconffeedback.utils.PreferenceKeys
import cat.cristina.pep.jbcnconffeedback.utils.SessionsTimes
import cat.cristina.pep.jbcnconffeedback.utils.TalksLocations
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import java.util.*

private val TAG = TalkContent::class.java.name

class TalkContent(val context: Context, val date: Date) {

    private var databaseHelper: DatabaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper::class.java)
    private var utilDAOImpl: UtilDAOImpl
    private var speakerDao: Dao<Speaker, String>
    private var talkDao: Dao<Talk, Int>
    //private var speakerTalkDao: Dao<SpeakerTalk, Int>

    /**
     * An array of talk items.
     */
    val ITEMS: MutableList<TalkItem> = mutableListOf()
    val ITEMS_FILTERED_BY_DATE_AND_ROOM_NAME: MutableList<TalkItem> = mutableListOf()

    /**
     * A map of pairs <scheduleId, TalkItem
     */
    val ITEM_MAP: MutableMap<String, TalkItem> = HashMap()

    init {
        utilDAOImpl = UtilDAOImpl(context, databaseHelper)
        talkDao = databaseHelper.getTalkDao()
        speakerDao = databaseHelper.getSpeakerDao()
        //speakerTalkDao = databaseHelper.getSpeakerTalkDao()

        var i = 1
        talkDao
                .queryForAll()
                .stream()
                // by scheduleId
                .sorted()
                .forEach {
                    addItem(createTalkItem(i++, it))
                }

    }

    private fun addItem(item: TalkItem) {

        /* ITEMS contiene todos  */
        ITEMS.add(item)

        /* Cada entrada de ITEM_MAP contiene un scheduleId y un talkitem   */
        ITEM_MAP.put(item.talk.scheduleId, item)

        val today = GregorianCalendar()
        today.time = date

        val scheduleId = item.talk.scheduleId
        val session = SessionsTimes.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(9, 12)}")
        val location = TalksLocations.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(5, 8)}")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val roomName =
                sharedPreferences?.getString(PreferenceKeys.ROOM_KEY, context.resources.getString(R.string.pref_default_room_name))
        if (today.get(Calendar.DATE) == session.getStartTalkDateTime().get(Calendar.DATE)
                && today.get(Calendar.MONTH) == session.getStartTalkDateTime().get(Calendar.MONTH)
                && today.get(Calendar.YEAR) == session.getStartTalkDateTime().get(Calendar.YEAR)
                && roomName == location.getRoomName()) {
            ITEMS_FILTERED_BY_DATE_AND_ROOM_NAME.add(item)
        }
    }

    private fun createTalkItem(position: Int, talk: Talk): TalkItem {
        val speakerRef: String = talk.speakers?.get(0) ?: "null"
        return TalkItem(position.toString(), talk, utilDAOImpl.lookupSpeakerByRef(speakerRef))
    }

    /* A talk item */
    data class TalkItem(val id: String, val talk: Talk, val speaker: Speaker)
}
