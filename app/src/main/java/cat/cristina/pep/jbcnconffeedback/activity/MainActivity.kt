package cat.cristina.pep.jbcnconffeedback.activity

import android.app.Dialog
import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.support.annotation.CallSuper
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
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
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


private const val SPEAKERS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"
private const val TALKS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json"

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
        AboutUsDialogFragment.AboutUsDialogFragmentListener,
        LicenseDialogFragment.LicenseDialogFragmentListener,
        DatePickerDialogFragment.DatePickerDialogFragmentListener {

    private val random = Random()
    private val DEFAULT_STATISTICS_FILE_NAME = "statistics.csv"
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
    private var autoMode: Boolean = false
    private val talkSchedules = HashMap<Talk, Pair<SessionsTimes, TalksLocations>>()
    // TODO("Delete in production")
    //private val setOfScheduleIds: MutableSet<String> = mutableSetOf()
    private lateinit var sharedPreferences: SharedPreferences
    private var filteredTalks = false
    //private lateinit var dialogFragment: DialogFragment
    private var dataFromFirestore: Map<Long?, List<QueryDocumentSnapshot>>? = null
    private var date = Date()
    private var isLogIn = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
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

        /* TODO("Uncomment in production")  */
        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {

            override fun onDrawerOpened(drawerView: View) {
                if (!isLogIn) {
                    super.onDrawerOpened(drawerView)
                    val dialogFragment = CredentialsDialogFragment.newInstance(MAIN_ACTIVITY, autoMode)
                    dialogFragment.show(supportFragmentManager, "CredentialsDialogFragment")
                }
            }
        })

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        databaseHelper = OpenHelperManager.getHelper(applicationContext, DatabaseHelper::class.java)
        utilDAOImpl = UtilDAOImpl(this, databaseHelper)
        val (connected, reason) = isDeviceConnectedToWifiOrData()
        if (connected)
            requestQueue = Volley.newRequestQueue(this)
        else
            Toast.makeText(applicationContext, "${resources.getString(R.string.sorry_working_offline)}: $reason", Toast.LENGTH_SHORT).show()
        setup(connected)

        nav_view.getHeaderView(0).setOnClickListener({
            if (autoMode == false) {

                val stackSize = supportFragmentManager.backStackEntryCount
                if (stackSize > 0) {
                    for (i in 1..stackSize) {
                        supportFragmentManager.popBackStack()
                    }
                    filteredTalks = sharedPreferences.getBoolean(PreferenceKeys.FILTERED_TALKS_KEY, false)
                    val fragment = ChooseTalkFragment.newInstance(1, filteredTalks)
                    switchFragment(fragment, CHOOSE_TALK_FRAGMENT, false)
                    closeLateralMenu()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")

    }

    override fun onRestart() {
        Log.d(TAG, "onRestart()")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
        requestQueue?.cancelAll(TAG)
        cancelTimer()
    }

    fun getAutoModeAndRoomName(): Pair<Boolean, String> =
            Pair(sharedPreferences.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false), sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name)))

    /*
    * This method does two things:
    *
    * - Starts the chain of downloads/parseing/database persistence
    *
    * - Check the working mode (auto/manual) and sets the appropriate fragment. Finally,
    *   it sets up the timers if necessary
    *
    * */
    private fun setup(downloadData: Boolean): Unit {

        /* setup si hay conexión  */
        if (downloadData) {

            dialog = ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT) // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            dialog.setMessage(resources.getString(R.string.loading))
            dialog.isIndeterminate = true
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            downloadSpeakers()

        } else {
            /* configuración del modo de trabajo */
            autoMode = getAutoModeAndRoomName().first
            roomName = getAutoModeAndRoomName().second

            if (autoMode) {

                if (roomName == resources.getString(R.string.pref_default_room_name)) {

                    sharedPreferences.edit().putBoolean(PreferenceKeys.AUTO_MODE_KEY, false).commit()
                    Toast.makeText(this, resources.getString(R.string.pref_default_room_name), Toast.LENGTH_SHORT).show()
                    val fragment = ChooseTalkFragment.newInstance(1)
                    switchFragment(fragment, CHOOSE_TALK_FRAGMENT, false)

                } else { // autoMode and roomName set

                    /*
                    * Aquesta estructura de dades associa cada talk amb un SessionTimes i un TalksLocation
                    * Es a dir, a qué hora es fa cada talk i on
                    * talkSchedules is a Map<Talk, Pair<SessionsTimes, TalksLocations>>
                    *
                    * */

                    val talkDao: Dao<Talk, Int> = databaseHelper.getTalkDao()
                    talkDao.queryForAll().forEach {
                        val scheduleId = it.scheduleId
                        //                   0123456789012
                        // scheduleId format #MON-TC1-SE1
                        val session = SessionsTimes.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(9, 12)}")
                        val location = TalksLocations.valueOf("${scheduleId.substring(1, 4)}_${scheduleId.substring(5, 8)}")
                        // Log.d(TAG, "$it $scheduleId $session $location")
                        // crea una mapa de Talk y Pair<SessionsTimes, TalksLocation>
                        talkSchedules.put(it, session to location)

                    }

                    setupTimer()

                }

            } else { // autoMode is false ->  mode manual

                // On start filteredTalks must be false to show all talks, no filter
//                filteredTalks = sharedPreferences.getBoolean(PreferenceKeys.FILTERED_TALKS_KEY, false)
                val fragment = ChooseTalkFragment.newInstance(1, filteredTalks, fromDateToString())

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
     * First we need to find the list of talks matching today and this room
     *
     * */
    private fun setupTimer() {

        val fragment = WelcomeFragment.newInstance(roomName, "")
        switchFragment(fragment, "$WELCOME_FRAGMENT$roomName", false)

        scheduledExecutorService = Executors.newScheduledThreadPool(5)
        scheduledFutures = mutableListOf()

        // val today = GregorianCalendar.getInstance()

        /* TODO("REMOVE in production") */

        val today = GregorianCalendar(2018, 6, 11, 9, 0)

        Log.d(TAG, "****** ${today.toString()} ******")

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

                        /* TODO("Uncomment in production")  */

//                        val startTime = pair.first.getStartTimeMinusOffset().time - System.currentTimeMillis()
//                        val endTime = pair.first.getEndTimePlusOffset().time - System.currentTimeMillis()

                        /* TODO("Remove in production")  */

                        val startTime = pair.first.getStartTimeMinusOffset().time - today.time.time
                        val endTime = pair.first.getEndTimePlusOffset().time - today.time.time

                        /* Aixo calcula el temps que queda a que comenci i acabi l'event actual considerant un offset de 15 minuts  */

                        val remainingStartTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(startTime),
                                TimeUnit.MILLISECONDS.toMinutes(startTime) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(startTime) % TimeUnit.MINUTES.toSeconds(1))

                        val remainingStopTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(endTime),
                                TimeUnit.MILLISECONDS.toMinutes(endTime) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(endTime) % TimeUnit.MINUTES.toSeconds(1))

                        /* Dos runnable, un que posara el fragment de votar i un altre que el treura  */

                        val timerTaskIn = Runnable {
                            Log.d(TAG, "VoteFragment... $talkId $talkTitle $talkAuthorName")
                            switchFragment(VoteFragment.newInstance("$talkId", talkTitle, talkAuthorName), VOTE_FRAGMENT, false)
                        }

                        val timerTaskOff = Runnable {
                            Log.d(TAG, "WelcomeFragment.........")
                            switchFragment(WelcomeFragment.newInstance(roomName, "used"), WELCOME_FRAGMENT, false)
                        }

                        /* Finalment posem en marxa el scheduler  */

                        scheduledFutures?.add(scheduledExecutorService?.schedule(timerTaskIn, startTime, TimeUnit.MILLISECONDS))
                        scheduledFutures?.add(scheduledExecutorService?.schedule(timerTaskOff, endTime, TimeUnit.MILLISECONDS))

                        Log.d(TAG, "Setting schedule for talk $talk starts in $remainingStartTime ends in $remainingStopTime")

                    }
                }
            }
        }

        //Toast.makeText(this, R.string.setting_timers, Toast.LENGTH_LONG).show()
        Toast.makeText(this, """${scheduledFutures?.size?.div(2)
                ?: "0"} timers set""", Toast.LENGTH_SHORT).show()

        /* Cerramos el executor service para que no se sirvan más tareas, pero las tareas pendientes no se cancelan  */

        scheduledExecutorService?.shutdown()
    }


    /*
    * This method changes de actual fragment for another fragment unless both have the samen tag
    * name. It will also add the fragment to the back stack or not, depending on the addToStack
    * parameter.
    *
    * */
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


    private fun fromDateToString() = SimpleDateFormat("dd/MM/yyyy").format(date)

    /*
    * This method downloads de JSON with all speaker. It uses an asynch call so I have
    * chained subsequent dependent calls.
    *
    * */
    private fun downloadSpeakers() {

        val speakersRequest: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, SPEAKERS_URL, null,

                Response.Listener { response ->
                    // Log.d(TAG, "Speakers Response: %s".format(response.toString()))
                    parseAndStoreSpeakers(response.toString())
                },

                Response.ErrorListener { error ->
                    if (dialog.isShowing)
                        dialog.dismiss()
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    //Log.e(TAG, error.message)
                })

        speakersRequest.tag = TAG

        /* This call won't block the main thread */

        requestQueue?.add(speakersRequest)
    }

    /*
    * This method first calls the downloadTalks() method to start the download of talks, meanwhile
    * it parses and persist the JSON data from the downloadSpeakers() method
    *
    * */
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
                // Log.d(TAG, "Speaker ${speaker} inserted")
            } catch (error: Exception) {
                /* duplicated generalment  */
                Log.e(TAG, "Could not insert speaker ${speaker} ${error.message}")
                if (dialog.isShowing)
                    dialog.dismiss()
            }
        }
    }

    /*
    * This method downloads the talks' JSON. Note that ALL speakers must be already persisted
    * in the internal data base before they get persisted.
    *
    * */
    private fun downloadTalks() {

        val talksRequest = JsonObjectRequest(Request.Method.GET, TALKS_URL, null,

                Response.Listener { response ->
                    // Log.d(TAG, "Talks Response: %s".format(response.toString()))
                    parseAndStoreTalks(response.toString())
                },

                Response.ErrorListener { error ->
                    // Log.e(TAG, error.message)

                    if (dialog.isShowing)
                        dialog.dismiss()

                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

                })

        talksRequest.tag = TAG

        requestQueue?.add(talksRequest)
    }

    /*
    * This method parses and stores talk's JSON in the local database
    *
    * */
    private fun parseAndStoreTalks(talksJson: String) {

        val json = JSONObject(talksJson)
        val items = json.getJSONArray("items")
        val talkDao: Dao<Talk, Int> = databaseHelper.getTalkDao()
        val speakerTalkDao: Dao<SpeakerTalk, Int> = databaseHelper.getSpeakerTalkDao()
        val gson = Gson()

        for (i in 0 until (items.length())) {

            val talkObject = items.getJSONObject(i)
            val talk: Talk = gson.fromJson(talkObject.toString(), Talk::class.java)

            try {
                /* Guardamos cada talk */
                talkDao.create(talk)
                // Log.d(TAG, "Talk ${talk} created")
            } catch (error: Exception) {
                if (dialog.isShowing)
                    dialog.dismiss()
                Log.e(TAG, "Could not insert talk ${talk.id} ${error.message}")
            }

            /* relacionamos cada talk con su speaker/s not necessary because there are no joins */
//            for (j in 0 until (talk.speakers!!.size)) {
//                val speakerRef: String = talk.speakers!!.get(j)
//                val dao: UtilDAOImpl = UtilDAOImpl(applicationContext, databaseHelper)
//                Log.d(TAG, "Looking for ref $speakerRef")
//                val speaker: Speaker = dao.lookupSpeakerByRef(speakerRef)
//                val speakerTalk = SpeakerTalk(0, speaker, talk)
//                try {
//                    speakerTalkDao.create(speakerTalk)
//                    Log.d(TAG, "Speaker-Talk ${speakerTalk}  from ${speaker} and  ${talk} created")
//                } catch (e: Exception) {
//                    Log.e(TAG, "Could not insert Speaker-Talk ${speakerTalk.id} ${e.message}")
//                }
//            }
        }

        if (dialog.isShowing)
            dialog.dismiss()

        //* setup de la parte sin conexión: en funcio de autoMode/roomName set or not  */

        setup(false)
    }

    /*
    * This method checks whether the device is connected or not
    *
    * */
    fun isDeviceConnectedToWifiOrData(): Pair<Boolean, String> {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val netInfo: NetworkInfo? = cm.activeNetworkInfo

        //return netInfo != null && netInfo.isConnectedOrConnecting()

        return Pair(netInfo?.isConnected ?: false, netInfo?.reason
                ?: resources.getString(R.string.sorry_not_connected))
    }

    private fun closeLateralMenu(): Unit {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    /*
    * This method manages the back button press events.
    *
    * According to StackOverflow:
    *
    * You can not control recent app AND home button in Android.
    * You can’t allow user to not leave the app. It is user’s choice.
    *
    * */
    override fun onBackPressed() {

        /* Close drawer is open  */

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {

            val actualFragment = supportFragmentManager.findFragmentById(R.id.contentFragment)

            /* aixo evita sortir de l'app amb el back button  */
            if (supportFragmentManager.backStackEntryCount == 0) {
                Toast.makeText(this, R.string.choose_finish_to_exit, Toast.LENGTH_SHORT).show()
                return
            }

            /* quan es mostra el votefragment backStackEntryCount es 1. VoteFragment te el su boto de sortir  */
            if (actualFragment?.tag == VOTE_FRAGMENT) {
                if (isLogIn) {
                    super.onBackPressed()
                }
                else {
                    val dialogFragment = CredentialsDialogFragment.newInstance(VOTE_FRAGMENT, autoMode)
                    dialogFragment.show(supportFragmentManager, CREDENTIALS_DIALOG_FRAGMENT)
                }
                return
            }

//            if (supportFragmentManager.backStackEntryCount == 1) {
//                return
//            }

            /* per sortir de l'app hi ha un menu finish  */
            super.onBackPressed()

        }
    }

    /*
    * You must return true for the menu to be displayed; if you return false it will not be shown
    * */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    /*
    * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logout -> {
                Toast.makeText(this, R.string.action_logout, Toast.LENGTH_SHORT).show()
                isLogIn = false
                invalidateOptionsMenu()
                return true
            }
        }

        return super.onOptionsItemSelected(item)

    }

    /*
    * This method is called every time the menus are shown
    *
    * */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val drawerMenu = nav_view.menu

        val itemUpdate = drawerMenu.findItem(R.id.action_update)
        itemUpdate?.isEnabled = databaseHelper.getScoreDao().queryForAll().size > 0

        val itemLogOut = menu?.findItem(R.id.action_logout)
        itemLogOut?.isEnabled = isLogIn
        if (isLogIn) {
            itemLogOut?.icon?.alpha = 255
        } else {
            itemLogOut?.icon?.alpha = 85
        }

        return true
    }


    /*
    * This method is called from onChooseTalkFragment when a talk es selected in manual mode
    *
    * */
    override fun onChooseTalk(item: TalkContent.TalkItem?) {

        val voteFragment = VoteFragment.newInstance(item?.talk?.id.toString(), item?.talk?.title!!, item.speaker.name)
        switchFragment(voteFragment, VOTE_FRAGMENT)
    }

    /*
    * This method is called from
    *
    * */
    override fun onUpdateTalks() {

        val scoreDao: Dao<Score, Int> = databaseHelper.getScoreDao()

        if (scoreDao.countOf() > 0) {
            if (isDeviceConnectedToWifiOrData().first) {

                Toast.makeText(this, R.string.success_data_updated, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, R.string.sorry_not_connected, Toast.LENGTH_SHORT).show()
            }
        } else { // no records
            Toast.makeText(this, R.string.sorry_no_local_data, Toast.LENGTH_SHORT).show()
        }
    }

    /*
    * Returns true to display the item as the selected item
    *
    * */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.action_statistics -> {
                val fragment = StatisticsFragment.newInstance()
                switchFragment(fragment, STATISTICS_FRAGMENT)
            }
            R.id.action_settings -> {
                //startActivity(Intent(this, SettingsActivity::class.java))
                val fragment = AppPreferenceFragment()
                switchFragment(fragment, SETTINGS_FRAGMENT)
            }
            R.id.action_send_statistics -> {
                downloadScoring()
            }
            R.id.action_update -> {
                onUpdateTalks()
            }
            R.id.action_finish -> {
                finishAndRemoveTask()
            }
            R.id.action_license -> {
                val licenseFragment = LicenseDialogFragment.newInstance("", "")
                licenseFragment.show(supportFragmentManager, LICENSE_DIALOG_FRAGMENT)
            }
            R.id.action_about_us -> {
                val aboutUsFragment = AboutUsDialogFragment.newInstance("", "")
                aboutUsFragment.show(supportFragmentManager, ABOUT_US_FRAGMENT)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }

    /*
    * This method downloads the Scoring collection made up of documents(id_talk, score, date)
    *
    * */
    private fun downloadScoring(): Unit {

        if (!isDeviceConnectedToWifiOrData().first) {
            Toast.makeText(this, R.string.sorry_not_connected, Toast.LENGTH_SHORT).show()
            return
        }

        dialog = ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setMessage(resources.getString(R.string.loading))
        dialog.isIndeterminate = true
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        FirebaseFirestore.getInstance()
                .collection("Scoring")
                .get()
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        dataFromFirestore = it.result.groupBy {
                            it.getLong("id_talk")
                        }
                        dialog.dismiss()
                        createCVSFromStatistics(DEFAULT_STATISTICS_FILE_NAME)
                    } else {
                        dialog.dismiss()
                        Toast.makeText(this, R.string.sorry_no_scoring_avaiable, Toast.LENGTH_SHORT).show()
                        //Log.d(TAG, "*** Error *** ${it.exception?.message}")
                    }
                }
    }

    /*
   * /storage/emulated/0/Android/data/cat.cristina.pep.jbcnconffeedback/files/Documents/statistics.csv
   *
   *
   * */
    private fun createCVSFromStatistics(fileName: String): Unit {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val fileWriter = FileWriter(file)

        val csvWriter = CSVWriter(fileWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)

        csvWriter.writeNext(arrayOf("id_talk", "title", "score", "date"))

        dataFromFirestore
                ?.asSequence()
                ?.forEach {
                    val idTalk = it.key
                    var title = databaseHelper.getTalkDao().queryForId(idTalk?.toInt()).title
                    title = title.replace(",", " ")
                    if (title.length > 30)
                        title = title.substring(0, 30) + " ..."
                    dataFromFirestore?.get(it.key)
                            ?.asSequence()
                            ?.forEach { doc: QueryDocumentSnapshot ->
                                val score = doc.get("score")
                                val date = doc.getDate("date")
                                Log.d(TAG, "$idTalk $title $score $date")
                                csvWriter.writeNext(arrayOf(idTalk.toString(), title, score.toString(), date.toString()))
                            }
                }

        csvWriter.close()
        sendCSVByEmail(DEFAULT_STATISTICS_FILE_NAME)
    }

    /*
    * This methods sends an email with the CSV file attached
    *
    * */
    private fun sendCSVByEmail(fileName: String): Unit {

        var emailAddress = arrayOf(sharedPreferences.getString(PreferenceKeys.EMAIL_KEY, resources.getString(R.string.pref_default_email)))
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.email_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.email_message))
        val uri = Uri.fromFile(file)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)

        val componentName = emailIntent.resolveActivity(packageManager)

        if (componentName != null)
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
        else
            Toast.makeText(this, R.string.sorry_no_app_to_attend_this_request, Toast.LENGTH_SHORT).show()

    }


    /*
    *
    * This method is called from ChooseTalkFragment when there's a filter request by date and room
    *
    * */
    override fun onFilterTalks(filtered: Boolean) {
        sharedPreferences.edit().putBoolean(PreferenceKeys.FILTERED_TALKS_KEY, filtered).commit()
        filteredTalks = filtered

        filteredTalks = sharedPreferences.getBoolean(PreferenceKeys.FILTERED_TALKS_KEY, false)
        val fragment = ChooseTalkFragment.newInstance(1, filteredTalks, fromDateToString())

        switchFragment(fragment, "$CHOOSE_TALK_FRAGMENT$filtered$date", false)


//        val fragment = ChooseTalkFragment.newInstance(1, filtered)
//        switchFragment(fragment, "$CHOOSE_TALK_FRAGMENT$filtered", false)
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

        if (sharedPreferences.getBoolean(PreferenceKeys.VIBRATOR_KEY, false)) {
            Log.d(TAG, "vibrating ......")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                vibrator.vibrate(250)
            }
        }
    }

    /*
    * This method cancels pending tasks
    *
    * */
    private fun cancelTimer() {
        if (scheduledFutures != null) {
            for (scheduledFuture in scheduledFutures!!) {
                scheduledFuture?.cancel(true)
            }
        }
    }


    /*
    * This method might be called from the StatisticsFragment
    * */
    override fun onStatisticsFragment(msg: String) {
    }

    /*
    * This method is called from the preference fragment, every time there is a change in
    * automode and/or roomName
    *
    * */
    override fun onAppPreferenceFragmentAutoModeRoomNameChanged(autoMode: Boolean) {

        roomName = sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name))
        this.autoMode = autoMode

        if (autoMode) {
            setup(false)
        } else {
            cancelTimer()
            filteredTalks = sharedPreferences.getBoolean(PreferenceKeys.FILTERED_TALKS_KEY, false)
            val fragment = ChooseTalkFragment.newInstance(1, filteredTalks, fromDateToString())
            switchFragment(fragment, "$CHOOSE_TALK_FRAGMENT$roomName", false)
        }

    }

    /*
    * This method might get called from WelcomeFragment eventually
    *
    * */
    override fun onWelcomeFragment(msg: String) {
    }

    /*
    * This method is called from the CredentialsDialogFragment to report the user interaction
    *
    * */
    override fun onCredentialsDialogFragmentInteraction(answer: Int) {
        when (answer) {
            Dialog.BUTTON_POSITIVE -> {
                Toast.makeText(this, R.string.action_login, Toast.LENGTH_SHORT).show()
                isLogIn = true
                invalidateOptionsMenu()
            }
            Dialog.BUTTON_NEGATIVE -> {
                closeLateralMenu()
            }
            Dialog.BUTTON_NEUTRAL -> {
            }
        }
    }

    /*
    * This method might get called from AboutUsDialogFragment eventually
    *
    * */
    override fun onAboutUsDialogFragmentInteraction(msg: String) {
        closeLateralMenu()
    }

    /*
    * This method might get called from LicenseDialogFragment eventually
    *
    * */
    override fun onLicenseDialogFragmentInteraction(msg: String) {
        closeLateralMenu()
    }

    /*
    * This method set de date/time to filter talks
    *
    * */
    override fun onDatePikerDialogFragmentInteraction(msg: String) {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        date = simpleDateFormat.parse(msg)
        val hour = GregorianCalendar().get(Calendar.HOUR_OF_DAY)
        val minutes = GregorianCalendar().get(Calendar.MINUTE)
        date.time = date.time + ((hour * 60 + minutes) * 60 * 1_000)

        filteredTalks = sharedPreferences.getBoolean(PreferenceKeys.FILTERED_TALKS_KEY, false)
        val fragment = ChooseTalkFragment.newInstance(1, filteredTalks, fromDateToString())

        switchFragment(fragment, CHOOSE_TALK_FRAGMENT + msg, false)
    }

    companion object {

        const val MAIN_ACTIVITY = "MainActivity"
        const val CHOOSE_TALK_FRAGMENT = "ChooseTalkFragment"
        const val STATISTICS_FRAGMENT = "StatisticsFragment"
        const val CREDENTIALS_DIALOG_FRAGMENT = "CredentialsDialogFragment"
        const val VOTE_FRAGMENT = "VoteFragment"
        const val WELCOME_FRAGMENT = "WelcomeFragment"
        const val SETTINGS_FRAGMENT = "SettingsFragment"
        const val ABOUT_US_FRAGMENT = "AboutUsFragment"
        const val LICENSE_DIALOG_FRAGMENT = "LicenseDialogFragment"
        const val SCHEDULE_FRAGMENT = "DatePickerDialogFragment"
        const val DATE_PICKER_FRAGMENT = "DatePickerFragment"

    }

}
