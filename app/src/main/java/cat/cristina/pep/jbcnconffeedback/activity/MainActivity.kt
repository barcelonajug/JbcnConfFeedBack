package cat.cristina.pep.jbcnconffeedback.activity

import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

private const val SPEAKERS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"
private const val TALKS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json"
private const val CHOOSE_TALK_FRAGMENT = "ChooseTalkFragment"
private const val STATISTICS_FRAGMENT = "StatisticsFragment"
private const val VOTE_FRAGMENT = "VoteFragment"
private const val FIREBASE_COLLECTION = "Scoring"
private const val FIREBASE_COLLECTION_FIELD_1 = "id_talk"
private const val FIREBASE_COLLECTION_FIELD_2 = "score"
private const val FIREBASE_COLLECTION_FIELD_3 = "date"

class MainActivity :
        AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        ChooseTalkFragment.OnChooseTalkListener,
        VoteFragment.OnVoteFragmentListener,
        StatisticsFragment.OnStatisticsFragmentListener {

    private val TAG = MainActivity::class.java.name

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var speakerDao: Dao<Speaker, Int>
    private lateinit var talkDao: Dao<Talk, Int>
    private lateinit var speakerTalkDao: Dao<SpeakerTalk, Int>
    private lateinit var requestQueue: RequestQueue
    private lateinit var vibrator: Vibrator
    private lateinit var dialog: ProgressDialog

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val (connected, reason) = isDeviceConnectedToWifiOrData()

        if (connected) {
            requestQueue = Volley.newRequestQueue(this)
            requestQueue
            databaseHelper = OpenHelperManager.getHelper(applicationContext, DatabaseHelper::class.java)
            setup(true)
            //downloadSpeakers()
        } else {
            setup(false)
            Toast.makeText(applicationContext, "Working off line: $reason", Toast.LENGTH_LONG).show()
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

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
                    Log.d(TAG, "Speakers Response: %s".format(response.toString()))
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
        requestQueue.add(speakersRequest)

    }

    private fun parseAndStoreSpeakers(speakersJson: String) {

        downloadTalks()

        val json = JSONObject(speakersJson)
        val items = json.getJSONArray("items")
        speakerDao = databaseHelper.getSpeakerDao()
        val gson = Gson()

        for (i in 0 until (items.length())) {
            val speakerObject = items.getJSONObject(i)
            val speaker: Speaker = gson.fromJson(speakerObject.toString(), Speaker::class.java)
            try {
                speakerDao.create(speaker)
                Log.e(TAG, "Speaker ${speaker.id} inserted")
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
        requestQueue.add(talksRequest)
    }

    /*
    * Note that to insert talks into the database speakers need to be present already
    * */
    fun parseAndStoreTalks(talksJson: String) {
        val json = JSONObject(talksJson)
        val items = json.getJSONArray("items")
        talkDao = databaseHelper.getTalkDao()
        speakerTalkDao = databaseHelper.getSpeakerTalkDao()
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
                val dao = UtilDAOImpl(applicationContext, databaseHelper)
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


    private fun isDeviceConnectedToWifiOrData(): Pair<Boolean, String> {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //return netInfo != null && netInfo.isConnectedOrConnecting()
        return Pair(netInfo?.isConnected ?: false, netInfo.reason)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        when (item.itemId) {
//            R.id.action_reload -> return true
//            else -> return super.onOptionsItemSelected(item)
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onChooseTalk(item: TalkContent.TalkItem?) {
        val voteFragment = VoteFragment.newInstance(item?.talk?.id.toString(), item?.talk?.title!!, item.speaker.name)
        switchFragment(voteFragment, VOTE_FRAGMENT)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.stadistics -> {
                val fragment = StatisticsFragment.newInstance()
                switchFragment(fragment, STATISTICS_FRAGMENT)
            }
            R.id.settings -> {
                //startActivity(Intent(this, SettingsActivity::class.java))
                val fragment = AppPreferenceFragment()
                switchFragment(fragment, STATISTICS_FRAGMENT)
            }
            R.id.about_us -> {
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_LONG).show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /*
    *
    * Note that documents in a collections can contain different sets of information, key-value pairs
    *
    * Documents within the same collection can all contain different fields or store different types
    * of data in those fields. However, it's a good idea to use the same fields and data types across
    * multiple documents, so that you can query the documents more easily
    *
    * */
    override fun onVoteFragment(id_talk: Int, score: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val scoringDoc = mapOf(FIREBASE_COLLECTION_FIELD_1 to id_talk, FIREBASE_COLLECTION_FIELD_2 to score, FIREBASE_COLLECTION_FIELD_3 to java.util.Date())
        firestore
                .collection(FIREBASE_COLLECTION)
                .add(scoringDoc)
                .addOnSuccessListener {
                    Log.d(TAG, "$scoringDoc added")
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message)
                }
        /* Some user feedback in the form of a light vibration. Oreo. Android 8.0. APIS 26-27 */
        if (sharedPreferences.getBoolean(PreferenceKeys.VIBRATOR_KEY, true)) {
            Log.d(TAG, "vibrando..........")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                vibrator.vibrate(250)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (requestQueue != null) {
            requestQueue?.cancelAll(TAG)
        }
    }

    /*
    *
    * */
    override fun onStatisticsFragment(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
