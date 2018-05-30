package cat.cristina.pep.jbcnconffeedback.activity

import android.app.Dialog
import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.CallSuper
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.fragment.*
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent
import cat.cristina.pep.jbcnconffeedback.model.*
import cat.cristina.pep.jbcnconffeedback.utils.PreferenceKeys
import cat.cristina.pep.jbcnconffeedback.utils.SessionsTimes
import cat.cristina.pep.jbcnconffeedback.utils.TalksLocations
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


private const val SPEAKERS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"
private const val TALKS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json"
private const val CHOOSE_TALK_FRAGMENT = "ChooseTalkFragment"
private const val STATISTICS_FRAGMENT = "StatisticsFragment"
private const val VOTE_FRAGMENT = "VoteFragment"
private const val WELLCOME_FRAGMENT = "WellcomeFragment"
private const val FIREBASE_COLLECTION = "Scoring"
private const val FIREBASE_COLLECTION_FIELD_1 = "id_talk"
private const val FIREBASE_COLLECTION_FIELD_2 = "score"
private const val FIREBASE_COLLECTION_FIELD_3 = "date"

private val TAG = MainActivity::class.java.name

/*
* The first time you run or debug your project in Android Studio,
* the IDE automatically creates the debug keystore and certificate in
* $HOME/.android/debug.keystore, and sets the keystore and key passwords.
*
* At some point I will have to sign the APK with my own certificate
* because the debug certificate is created by the build tools and is insecure by design,
* most app stores (including the Google Play Store) will not accept an APK signed with a
* debug certificate for publishing..
*
* keytool -genkey -v -keystore android.keystore \
-keyalg RSA -keysize 2048 -validity 10000 -alias mendez
*
* mendez/valverde
*
* keytool -exportcert -list -v \
-alias androiddebugkey -keystore ~/.android/debug.keystore
*
* androiddebugkey/android
* */
class MainActivity :
        AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        ChooseTalkFragment.OnChooseTalkListener,
        VoteFragment.OnVoteFragmentListener,
        AppPreferenceFragment.OnAppPreferenceFragmentListener,
        StatisticsFragment.OnStatisticsFragmentListener,
        WelcomeFragment.OnWelcomeFragmentListener,
        CredentialsDialogFragment.CredentialsDialogFragmentListener,
        AboutUsDialogFragment.AboutUsDialogFragmentListener {

    private val random = Random()

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var utilDAOImpl: UtilDAOImpl
    private var requestQueue: RequestQueue? = null
    private lateinit var vibrator: Vibrator
    private lateinit var dialog: ProgressDialog
    // private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var scheduledFutures: MutableList<ScheduledFuture<*>?>? = null
    //private var timer: Timer? = null
    private lateinit var roomName: String
    private var autoMode: Boolean = true
    private val talkSchedules = HashMap<Talk, Pair<SessionsTimes, TalksLocations>>()
    // TODO("Delete in production")
    private val setOfScheduleIds: MutableSet<String> = mutableSetOf()
    private lateinit var sharedPreferences: SharedPreferences
    private var filteredTalks = true
    private lateinit var dialogFragment: DialogFragment


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*
        * int STATE_SETTLING Indicates that a drawer is in the process of settling to a final position.
        * int STATE_DRAGGING Indicates that a drawer is currently being dragged by the user.
        * int STATE_IDLE Indicates that any drawers are in an idle, settled state. No animation is in progress.
        *
        * */
        drawer_layout.addDrawerListener(object: DrawerLayout.SimpleDrawerListener(){

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                dialogFragment = CredentialsDialogFragment.newInstance("", "")
                dialogFragment.show(supportFragmentManager, "DialogFragment")

            }

        })

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        databaseHelper = OpenHelperManager.getHelper(applicationContext, DatabaseHelper::class.java)

        utilDAOImpl = UtilDAOImpl(this, databaseHelper)

    }

    override fun onStart() {
        super.onStart()

        val (connected, reason) = isDeviceConnectedToWifiOrData()

        if (connected) {
            requestQueue = Volley.newRequestQueue(this)
        } else {
            Toast.makeText(applicationContext, "${resources.getString(R.string.sorry_working_offline)}: $reason", Toast.LENGTH_LONG).show()
        }

        setup(connected)

        /* TODO("Remove in production")  */
        generateScheduleId()
    }

    /*
    * Note that to insert talks into the database speakers need to be present already
    * */
    fun parseAndStoreTalks(talksJson: String) {
        val json = JSONObject(talksJson)
        val items = json.getJSONArray("items")
        val talkDao: Dao<Talk, Int> = databaseHelper.getTalkDao()
        val speakerTalkDao: Dao<SpeakerTalk, Int> = databaseHelper.getSpeakerTalkDao()
        val gson = Gson()

        for (i in 0 until (items.length())) {
            val talkObject = items.getJSONObject(i)
            val talk: Talk = gson.fromJson(talkObject.toString(), Talk::class.java)

            try {
                /* TODO("Delete this line") */
                talk.scheduleId = getRandomScheduleId()
                /* Guardamos cada talk */
                talkDao.create(talk)
                Log.d(TAG, "Talk ${talk} created")
            } catch (e: Exception) {
                Log.e(TAG, "Could not insert talk ${talk.id} ${e.message}")
            }

            /* relacionamos cada talk con su speaker/s  */
            for (j in 0 until (talk.speakers!!.size)) {
                val speakerRef: String = talk.speakers!!.get(j)
                val dao: UtilDAOImpl = UtilDAOImpl(applicationContext, databaseHelper)
                Log.d(TAG, "Looking for ref $speakerRef")
                val speaker: Speaker = dao.lookupSpeakerByRef(speakerRef)
                val speakerTalk = SpeakerTalk(0, speaker, talk)
                try {
                    speakerTalkDao.create(speakerTalk)
                    Log.d(TAG, "Speaker-Talk ${speakerTalk}  from ${speaker} and  ${talk} created")
                } catch (e: Exception) {
                    Log.e(TAG, "Could not insert Speaker-Talk ${speakerTalk.id} ${e.message}")
                }
            }
        }
        dialog.dismiss()
        setup(false)
    }


    fun getAutoModeAndRoomName(): Pair<Boolean, String> =
            Pair(sharedPreferences.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false), sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name)))

    /*
    * This method downloads speakers and talks if there is a suitable connection. It also
    * has into account autoMode and roomName to present the right initial fragment
    * */
    private fun setup(downloadData: Boolean): Unit {

        if (downloadData) {
            dialog = ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT) // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            dialog.setMessage(resources.getString(R.string.loading))
            dialog.isIndeterminate = true
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            downloadSpeakers()
        } else {
            /*
           * Un cop que les dades estan assentades a la base de dades local desde el servidor
           * posem el fragment segons el mode de treball auto/manual.
           *
           * */

            autoMode = getAutoModeAndRoomName().first

            roomName = getAutoModeAndRoomName().second

            if (autoMode) {

                if (roomName == resources.getString(R.string.pref_default_room_name)) {
                    sharedPreferences.edit().putBoolean(PreferenceKeys.AUTO_MODE_KEY, false)
                    Toast.makeText(this, resources.getString(R.string.pref_default_room_name), Toast.LENGTH_LONG).show()
                    val fragment = ChooseTalkFragment.newInstance(1)
                    switchFragment(fragment, CHOOSE_TALK_FRAGMENT, false)
                } else { // autoMode and roomName set
                    // talkSchedules is a Map<Talk, Pair<SessionsTimes, TalksLocations>>
                    val talkDao: Dao<Talk, Int> = databaseHelper.getTalkDao()
                    talkDao.queryForAll().forEach {
                        val scheduleId = it.scheduleId
                        //                   0123456789012
                        // scheduleId format #MON-TC1-SE1
                        val session = SessionsTimes.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(9, 12)}")
                        val location = TalksLocations.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(5, 8)}")
                        Log.d(TAG, "$it $scheduleId $session $location")
                        // crea una mapa de Talk y Pair<SessionsTimes, TalksLocation>
                        talkSchedules.put(it, session to location)
                    }
                    setupTimer()
                }
            } else { // autoMode is false (manual)
                filteredTalks = sharedPreferences.getBoolean(PreferenceKeys.FILTERED_TALKS_KEY, false)
                val fragment = ChooseTalkFragment.newInstance(1, filteredTalks)
                switchFragment(fragment, CHOOSE_TALK_FRAGMENT, false)
            }
        }
    }

    /*
       * Set timers according to date/time and room name, one task per pending talk.
       *
       * Each task will show voting fragment with talk id, talk title, and author 15 minutes
       * before conclusion and will show welcome fragment back 15 times after conclusion.
       *
       * So we need to find the list of talks matching today and this room
       *
       * */
    private fun setupTimer() {

        val fragment = WelcomeFragment.newInstance(roomName, "")
        switchFragment(fragment, "$WELLCOME_FRAGMENT$roomName", false)
        Toast.makeText(this, "Setting timers...", Toast.LENGTH_LONG).show()
        scheduledExecutorService = Executors.newScheduledThreadPool(5)
        scheduledFutures = mutableListOf()
        // val today = GregorianCalendar.getInstance()
        /* TODO("REMOVE in production") */
        val today = GregorianCalendar(2018, 6, 11, 9, 0)

        talkSchedules.forEach { talk: Talk, pair: Pair<SessionsTimes, TalksLocations> ->
            /* roomName es el nom de la room que gestionas aquesta tablet */
            if (roomName == pair.second.getRoomName()) {
                /* Aixo evita schedules amb initialDelays negatius que faria iniciar els thread inmediatament  */
                if (today.before(pair.first.getStartTime())) {
                    if (today.get(Calendar.YEAR) == pair.first.getStartTime().get(Calendar.YEAR)
                            && today.get(Calendar.MONTH) == pair.first.getStartTime().get(Calendar.MONTH)
                            && today.get(Calendar.DATE) == pair.first.getStartTime().get(Calendar.DATE)) {
                        // compare today amb les dates de cada talk pero nomes dia, mes i any
                        val talkId = talk.id
                        val talkTitle = talk.title
                        val talkAuthorRef = talk.speakers?.get(0) ?: "Unknown"
                        val talkAuthor = utilDAOImpl.lookupSpeakerByRef(talkAuthorRef)
                        val talkAuthorName = talkAuthor.name
//                        val startTime = pair.first.getStartTimeMinusOffset().time - System.currentTimeMillis()
//                        val endTime = pair.first.getEndTimePlusOffset().time - System.currentTimeMillis()
                        /* TODO("Remove in production")  */
                        val startTime = pair.first.getStartTimeMinusOffset().time - today.time.time
                        val endTime = pair.first.getEndTimePlusOffset().time - today.time.time

                        val remainingStartTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(startTime),
                                TimeUnit.MILLISECONDS.toMinutes(startTime) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(startTime) % TimeUnit.MINUTES.toSeconds(1))

                        val remainingStopTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(endTime),
                                TimeUnit.MILLISECONDS.toMinutes(endTime) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(endTime) % TimeUnit.MINUTES.toSeconds(1))

                        val timerTaskIn = Runnable {
                            Log.d(TAG, "VoteFragment........")
                            switchFragment(VoteFragment.newInstance("$talkId", talkTitle, talkAuthorName), "VoteFragment", false)
                        }
                        // TODO("Pass arguments to Wellcome Fragment?")
                        val timerTaskOff = Runnable {
                            Log.d(TAG, "WelcomeFragment.........")
                            switchFragment(WelcomeFragment.newInstance(roomName, "used"), "WelcomeFragment", false)
                        }
                        Log.d(TAG, "Setting schedule for talk  $talkId $talkTitle starts in $remainingStartTime ends in $remainingStopTime")
                        scheduledFutures?.add(scheduledExecutorService?.schedule(timerTaskIn, startTime, TimeUnit.MILLISECONDS))
                        scheduledFutures?.add(scheduledExecutorService?.schedule(timerTaskOff, endTime, TimeUnit.MILLISECONDS))

                    }
                }
            }
        }

        scheduledExecutorService?.shutdown()
    }

    private fun switchFragment(fragment: Fragment, tag: String, addToStack: Boolean = true): Unit {

        val actualFragment = supportFragmentManager.findFragmentByTag(tag)

        actualFragment?.tag.run {
            if (this != tag) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFragment, fragment, tag)
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                if (addToStack)
                    transaction.addToBackStack(tag)
                transaction.commit()
            }
        }

    }

    private fun downloadSpeakers() {


        val speakersRequest: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, SPEAKERS_URL, null,
                Response.Listener { response ->
                    Log.d(TAG, "Speakers Response: %s".format(response.toString()))
                    parseAndStoreSpeakers(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.message)
                })
        speakersRequest.tag = TAG
        /*
        * When you call add(), Volley runs one cache processing thread and a pool of network dispatch threads.
        * When you add a request to the queue, it is picked up by the cache thread and triaged:
        * if the request can be serviced from cache, the cached response is parsed on the cache
        * thread and the parsed response is delivered on the main thread.
        *
        * If the request cannot be serviced from cache, it is placed on the network queue.
        *
        * The first available network thread takes the request from the queue,
        * performs the HTTP transaction, parses the response on the worker thread,
        * writes the response to cache, and posts the parsed response back to the main thread for delivery.
        *
        * Note that expensive operations like blocking I/O and parsing/decoding are done on worker threads.
        * You can add a request from any thread, but responses are always delivered on the main thread.
        *
        * No bloque el main thread
        *
        * */
        requestQueue?.add(speakersRequest)

    }

    private fun parseAndStoreSpeakers(speakersJson: String) {

        downloadTalks()

        val json = JSONObject(speakersJson)
        val items = json.getJSONArray("items")
        val speakerDao: Dao<Speaker, Int> = databaseHelper.getSpeakerDao()
        val gson = Gson()
        for (i in 0 until (items.length())) {
            val speakerObject = items.getJSONObject(i)
            val speaker: Speaker = gson.fromJson(speakerObject.toString(), Speaker::class.java)
            try {
                speakerDao.create(speaker)
                Log.d(TAG, "Speaker ${speaker} inserted")
            } catch (e: Exception) {
                Log.e(TAG, "Could not insert speaker ${speaker} ${e.message}")
            }
        }
    }

    private fun downloadTalks() {

        val talksRequest = JsonObjectRequest(Request.Method.GET, TALKS_URL, null,
                Response.Listener { response ->
                    Log.d(TAG, "Talks Response: %s".format(response.toString()))
                    parseAndStoreTalks(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, error.message)
                })
        talksRequest.tag = TAG
        requestQueue?.add(talksRequest)
    }

    /*
    * TODO("Delete in production")
    *
    * Format: #MON-TC1-SE1
    *
    * */
    private fun generateScheduleId(): Unit {

        for (room in 1..6) {
            for (session in 1..7) {
                setOfScheduleIds.add("#MON-TC$room-SE$session")
            }
        }
        for (room in 1..6) {
            for (session in 1..8) {
                setOfScheduleIds.add("#TUE-TC$room-SE$session")
            }
        }
        for (room in 1..2) {
            for (session in 1..2) {
                setOfScheduleIds.add("#WED-TC$room-SE$session")
            }
        }

    }

    /* TODO("Delete in production")  */
    private fun getRandomScheduleId(): String {
        val rnd = random.nextInt(setOfScheduleIds.size)
        val scheduleId = setOfScheduleIds.elementAt(rnd)
        setOfScheduleIds.remove(scheduleId)
        return scheduleId
    }


    public fun isDeviceConnectedToWifiOrData(): Pair<Boolean, String> {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo? = cm.activeNetworkInfo
        //return netInfo != null && netInfo.isConnectedOrConnecting()
        return Pair(netInfo?.isConnected ?: false, netInfo?.reason ?: "No connection available")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val actualFragment = supportFragmentManager.findFragmentById(R.id.contentFragment)

            actualFragment?.tag.run {
                if (this != VOTE_FRAGMENT) {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /*
    * This far, the main activity is not responding to any menu this method is so redundant.
    * Keep it for future use
    * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }



    /* From onChooseTalk  */
    override fun onChooseTalk(item: TalkContent.TalkItem?) {
        val voteFragment = VoteFragment.newInstance(item?.talk?.id.toString(), item?.talk?.title!!, item.speaker.name)
        switchFragment(voteFragment, VOTE_FRAGMENT)
    }

    /* From onChooseTalk  */
    override fun onUpdateTalks() {

        val scoreDao: Dao<Score, Int> = databaseHelper.getScoreDao()

        if (scoreDao.countOf() > 0) {

            if (isDeviceConnectedToWifiOrData().first) {

                Toast.makeText(this, R.string.success_data_updated, Toast.LENGTH_LONG).show()

                val firestore = FirebaseFirestore.getInstance()

                scoreDao.queryForAll().forEach {
                    val id = it.id
                    val scoringDoc = mapOf(FIREBASE_COLLECTION_FIELD_1 to it.id_talk,
                            FIREBASE_COLLECTION_FIELD_2 to it.score,
                            FIREBASE_COLLECTION_FIELD_3 to it.date)

                    firestore
                            .collection(FIREBASE_COLLECTION)
                            .add(scoringDoc)
                            .addOnSuccessListener {
                                scoreDao.deleteById(id)
                                Log.d(TAG, "$scoringDoc updated and removed")
                            }
                            .addOnFailureListener {
                                Log.d(TAG, it.message)
                            }

                }

            } else { // no connection
                Toast.makeText(this, R.string.sorry_not_connected, Toast.LENGTH_LONG).show()
            }
        } else { // no records
            Toast.makeText(this, R.string.sorry_no_local_data, Toast.LENGTH_LONG).show()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.statistics -> {
                val fragment = StatisticsFragment.newInstance()
                switchFragment(fragment, STATISTICS_FRAGMENT)
            }
            R.id.settings -> {
                //startActivity(Intent(this, SettingsActivity::class.java))
                val fragment = AppPreferenceFragment()
                switchFragment(fragment, STATISTICS_FRAGMENT)
            }
            R.id.about_us -> {
                val aboutUsFragment = AboutUsDialogFragment.newInstance("", "")
                aboutUsFragment.show(supportFragmentManager, "AboutUsDialogFragment")

//                val alertDialogBuilder = AlertDialog.Builder(this, R.style.Base_V7_Theme_AppCompat_Dialog)
//                alertDialogBuilder.setTitle(R.string.about_us_title)
//                alertDialogBuilder.setMessage(R.string.about_us_authors)
//                alertDialogBuilder.setPositiveButton(R.string.about_us_positive_button) { dialog, _ ->
//                    dialog.dismiss()
//                }
//                alertDialogBuilder.create().show()

                return true

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /*
    * This method is called from ChooseTalkFragment when there's a filter request by date and room
    *
    * */
    override fun onFilterTalks(filtered: Boolean) {
        sharedPreferences.edit().putBoolean(PreferenceKeys.FILTERED_TALKS_KEY, filtered).commit()
        filteredTalks = filtered
        val fragment = ChooseTalkFragment.newInstance(1, filtered)
        switchFragment(fragment, "$CHOOSE_TALK_FRAGMENT$filtered", false)
    }

    /*
    *
    * Note that documents in a collections can contain different sets of information, key-value pairs
    *
    * Documents within the same collection can all contain different fields or store different types
    * of data in those fields. However, it's a good idea to use the same fields and data types across
    * multiple documents, so that you can query the documents more easily
    *
    * Scoring: id_talk, score, date
    *
    * */
    override fun onVoteFragment(id_talk: Int, score: Int) {

        val scoreDao: Dao<Score, Int> = databaseHelper.getScoreDao()

        if (isDeviceConnectedToWifiOrData().first) {
            val firestore = FirebaseFirestore.getInstance()

            val scoringDoc = mapOf(FIREBASE_COLLECTION_FIELD_1 to id_talk,
                    FIREBASE_COLLECTION_FIELD_2 to score,
                    FIREBASE_COLLECTION_FIELD_3 to java.util.Date())

            firestore
                    .collection(FIREBASE_COLLECTION)
                    .add(scoringDoc)
                    .addOnSuccessListener {
                        // Log.d(TAG, "$scoringDoc added")
                    }
                    .addOnFailureListener {
                        scoreDao.create(Score(0, id_talk, score, Date()))
                        // Log.d(TAG, it.message)
                    }
        } else {
            val scoreObj = Score(0, id_talk, score, Date())
            scoreDao.create(scoreObj)
            Log.d(TAG, scoreObj.toString())
        }

        /* Some user feedback in the form of a light vibration. Oreo. Android 8.0. APIS 26-27 */
        if (sharedPreferences.getBoolean(PreferenceKeys.VIBRATOR_KEY, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                vibrator.vibrate(250)
            }
        }
    }

    private fun cancelTimer() {
        if (scheduledFutures != null) {
            for (scheduledFuture in scheduledFutures!!) {
                scheduledFuture?.cancel(true)
            }
        }
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        requestQueue?.cancelAll(TAG)
        cancelTimer()
    }

    /*
    *
    * */
    override fun onStatisticsFragment(msg: String) {
    }

    /*
    *
    * */
    override fun onAppPreferenceFragment(autoMode: Boolean) {
        roomName = sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name))
        if (autoMode) {
            setupTimer()
        } else {
            cancelTimer()
            switchFragment(ChooseTalkFragment.newInstance(1, filteredTalks), "$CHOOSE_TALK_FRAGMENT$roomName", false)
        }
    }

    override fun onWelcomeFragment(msg: String) {
    }

    override fun onCredentialsDialogFragmentInteraction(answer: Int) {
        when(answer) {
            Dialog.BUTTON_POSITIVE -> {
                // do nothing
            }
            Dialog.BUTTON_NEGATIVE -> {
                drawer_layout.closeDrawer(Gravity.LEFT)
            }
        }
    }

    override fun onAboutUsDialogFragmentInteraction(msg: String) {
    }

}
