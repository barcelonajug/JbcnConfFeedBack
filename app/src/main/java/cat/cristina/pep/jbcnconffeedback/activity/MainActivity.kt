package cat.cristina.pep.jbcnconffeedback.activity

import android.app.FragmentTransaction
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.fragment.ChooseTalkFragment
import cat.cristina.pep.jbcnconffeedback.fragment.StatisticsFragment
import cat.cristina.pep.jbcnconffeedback.fragment.VoteFragment
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent
import cat.cristina.pep.jbcnconffeedback.model.*
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

private val TAG = MainActivity::class.java.name
private const val SPEAKERS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"
private const val TALKS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json"

class MainActivity :
        AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        ChooseTalkFragment.OnChooseTalkListener,
        VoteFragment.OnVoteFragmentListener,
        StatisticsFragment.OnStatisticsFragmentListener {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var speakerDao: Dao<Speaker, Int>
    private lateinit var talkDao: Dao<Talk, Int>
    private lateinit var speakerTalkDao: Dao<SpeakerTalk, Int>
    private lateinit var requestQueue: RequestQueue
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if (isDeviceConnectedToWifiOrData()) {
            requestQueue = Volley.newRequestQueue(this)
            databaseHelper = OpenHelperManager.getHelper(applicationContext, DatabaseHelper::class.java)
            retrieveSpeakersFromWeb()
        } else {
            Toast.makeText(applicationContext, "There is no network connection try later.", Toast.LENGTH_LONG).show()
        }

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


    }

    private fun switchFragment(fragment: Fragment, addToStack: Boolean = true): Unit {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFragment, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addToStack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun retrieveSpeakersFromWeb() {

        val speakersRequest: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, SPEAKERS_URL, null,
                Response.Listener { response ->
                    Log.d(TAG, "Speakers Response: %s".format(response.toString()))
                    parseSpeakers(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, error.toString())
                })
        requestQueue.add(speakersRequest)

    }

    private fun parseSpeakers(speakers: String) {

        retrieveTalksFromWeb()

        val json = JSONObject(speakers)
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

    private fun retrieveTalksFromWeb() {

        val talksRequest = JsonObjectRequest(Request.Method.GET, TALKS_URL, null,
                Response.Listener { response ->
                    Log.d(TAG, "Talks Response: %s".format(response.toString()))
                    parseTalks(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, error.toString())
                })
        requestQueue.add(talksRequest)
    }

    fun parseTalks(talks: String) {
        val json = JSONObject(talks)
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
                val speakerTalk = SpeakerTalk(
                        0,
                        speaker,
                        talk
                )
                try {
                    speakerTalkDao.create(speakerTalk)
                    Log.e(TAG, "Speaker-Talk ${speakerTalk.id} created")
                } catch (e: Exception) {
                    Log.e(TAG, "Could not insert Speaker-Talk ${speakerTalk.id}")
                }
            }


        }

        /*
        * Un cop que les dades estan assentades a la base de dades local desde el servidor
        * posem el fragment.
        *
        * */
        val chooseTalkFragment = ChooseTalkFragment.newInstance(1)
        switchFragment(chooseTalkFragment, false)
    }


    private fun isDeviceConnectedToWifiOrData(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onChooseTalk(item: TalkContent.TalkItem?) {
        val voteFragment = VoteFragment.newInstance(item?.talk?.id.toString(), item?.talk?.title!!, item?.speaker?.name)
        switchFragment(voteFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            /* This menu handles statistics */
            R.id.nav_camera -> {
                val fragment = StatisticsFragment.newInstance()
                switchFragment(fragment, true)
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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
        val scoringDoc = mapOf("id_talk" to id_talk, "score" to score, "date" to java.util.Date())
        firestore
                .collection("Scoring")
                .add(scoringDoc)
                .addOnSuccessListener {
                    Log.d(TAG, "$scoringDoc added")
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message)
                }
        /* Some user feedback in the form of a light vibration. Oreo. Android 8.0. APIS 26-27 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            vibrator.vibrate(250)
        }
    }

    /*
    *
    * */
    override fun onStatisticsFragment(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
