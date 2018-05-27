package cat.cristina.pep.jbcnconffeedback.activity

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
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.fragment.AppPreferenceFragment
import cat.cristina.pep.jbcnconffeedback.fragment.ChooseTalkFragment
import cat.cristina.pep.jbcnconffeedback.fragment.StatisticsFragment
import cat.cristina.pep.jbcnconffeedback.fragment.VoteFragment
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent
import cat.cristina.pep.jbcnconffeedback.model.*
import cat.cristina.pep.jbcnconffeedback.utils.PreferenceKeys
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.*


private const val SPEAKERS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"
private const val TALKS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json"
private const val CHOOSE_TALK_FRAGMENT = "ChooseTalkFragment"
private const val STATISTICS_FRAGMENT = "StatisticsFragment"
private const val VOTE_FRAGMENT = "VoteFragment"
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
        StatisticsFragment.OnStatisticsFragmentListener {

    private lateinit var databaseHelper: DatabaseHelper
    private var requestQueue: RequestQueue? = null
    private lateinit var vibrator: Vibrator
    private lateinit var dialog: ProgressDialog
    private var timer: Timer? = null

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val (connected, reason) = isDeviceConnectedToWifiOrData()

        if (connected) {
            requestQueue = Volley.newRequestQueue(this)
            setup(true)
            //downloadSpeakers()

        } else {
            setup(false)
            Toast.makeText(applicationContext, "${resources.getString(R.string.sorry_working_offline)}: $reason", Toast.LENGTH_LONG).show()
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        databaseHelper = OpenHelperManager.getHelper(applicationContext, DatabaseHelper::class.java)

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

    private fun setup(isConnected: Boolean): Unit {

        if (isConnected) {
            dialog = ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(resources.getString(R.string.loading));
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show()
            downloadSpeakers()
        } else {
            /*
           * Un cop que les dades estan assentades a la base de dades local desde el servidor
           * posem el fragment.
           *
           * */
            val chooseTalkFragment = ChooseTalkFragment.newInstance(1)
            switchFragment(chooseTalkFragment, CHOOSE_TALK_FRAGMENT, false)
        }
    }

    private fun downloadSpeakers() {


        val speakersRequest: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, SPEAKERS_URL, null,
                Response.Listener { response ->
                    //Log.d(TAG, "Speakers Response: %s".format(response.toString()))
                    parseAndStoreSpeakers(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, error.toString())
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
                //Log.e(TAG, "Speaker ${speaker.id} inserted")
            } catch (e: Exception) {
                Log.e(TAG, "Could not insert speaker ${speaker.id} ${e.message}")
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
                    Log.d(TAG, error.toString())
                })
        talksRequest.tag = TAG
        requestQueue?.add(talksRequest)
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
                /* Guardamos cada talk */
                talkDao.create(talk)
                Log.e(TAG, "Talk ${talk.id} created")
            } catch (e: Exception) {
                Log.e(TAG, "Could not insert talk ${talk.id}")
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
                    Log.e(TAG, "Speaker-Talk ${speakerTalk.id} created")
                } catch (e: Exception) {
                    Log.e(TAG, "Could not insert Speaker-Talk ${speakerTalk.id}")
                }
            }
        }
        dialog.dismiss()
        setup(false)
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
    * This far, the main activity is not responding to any menu
    * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
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
                val alertDialogBuilder = AlertDialog.Builder(this, R.style.Base_V7_Theme_AppCompat_Dialog)
                alertDialogBuilder.setTitle(R.string.about_us_title)
                alertDialogBuilder.setMessage(R.string.about_us_authors)
                alertDialogBuilder.setPositiveButton(R.string.about_us_positive_button) { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialogBuilder.create().show()

                return true
            }
            R.id.email -> {
                createCSVFileExternalStorage()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

//    fun writeCsv() {
//        val fileName = "talks.csv"
//
//        try {
//            val file = File(filesDir, fileName)
//            val fileWriter: FileWriter = FileWriter(file)
//            val csvWriter: CSVWriter = CSVWriter(fileWriter, CSVWriter.DEFAULT_SEPARATOR,
//                    CSVWriter.NO_QUOTE_CHARACTER,
//                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                    CSVWriter.DEFAULT_LINE_END)
//
//            csvWriter.writeNext(arrayOf("Cris", "cris@hotmail.com", "+1-1111111112", "Spain"))
//            csvWriter.writeNext(arrayOf("Pep", "pep@hotmail.com", "+1-1111111112", "Spain"))
//            csvWriter.flush()
//            csvWriter.close()
//            Toast.makeText(this, "ECG values saved", Toast.LENGTH_SHORT).show()
//
//            //val contentUri = FileProvider.getUriForFile(applicationContext, "cat.cristina.pep.jbcnconffeedback", file)
//
//            var to = arrayOf(sharedPreferences.getString(PreferenceKeys.EMAIL, ""))
//
//            val emailIntent = Intent(Intent.ACTION_SEND)
//            emailIntent.type = "text/plain"
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.email_subject))
//            emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.email_message))
//            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            emailIntent.putExtra(Intent.EXTRA_STREAM, file.toURI())
//            startActivity(emailIntent)
//        } catch (e: IOException) {
//            Toast.makeText(this, "Error while saving values", Toast.LENGTH_SHORT).show()
//            Log.e("error", "" + e.message)
//            Log.e("error", "" + e.stackTrace)
//        }
//    }

    /**
     * /storage/emulated/0/Android/data/org.escoladeltreball.schedulerdemo1/files/Documents/data.csv
     */
    private fun createCSVFileExternalStorage(): Unit {
        val fileName = "talks.csv"
        try {
            // Get the directory for the user's public pictures directory.
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val writer = FileWriter(file)
            val csvWriter = CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)

            csvWriter.writeNext(arrayOf("Name", "Email", "Phone", "Country"))
            csvWriter.writeNext(arrayOf("pep", "pep@gmail.com", "100000000", "catalunya"))
            csvWriter.writeNext(arrayOf("cris", "cris@gmail.com", "111111111", "spain"))
            csvWriter.flush()
            csvWriter.close()
            if (!file.exists())
                Toast.makeText(this, "$file no exists", Toast.LENGTH_LONG).show()

            Log.d(TAG, "${file.absolutePath}")

            sendCSVByEmail(fileName)
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
    }

    private fun sendCSVByEmail(fileName: String): Unit {
        var emailAddress = arrayOf(sharedPreferences.getString(PreferenceKeys.EMAIL, ""))
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.email_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.email_message))
        val uri = Uri.fromFile(file)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
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

    @CallSuper
    override fun onStop() {
        super.onStop()
        requestQueue?.cancelAll(TAG)
        timer?.cancel()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupTimer(autoMode: Boolean) {
        if (autoMode) {
            timer = Timer("autoMode")
            timer?.scheduleAtFixedRate(object : TimerTask() {
                /* The action to be performed by this timer task */
                override fun run() {
                    Log.d(TAG, "in timer")
                }

            }, Date(), 1_000)
        } else {
            timer?.cancel()
            timer = null
        }

    }

    /*
    *
    * */
    override fun onStatisticsFragment(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*
    *
    * */
    override fun onAppPreferenceFragment(autoMode: Boolean) {
        setupTimer(autoMode)
    }


}
