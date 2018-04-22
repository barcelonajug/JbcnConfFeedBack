package cat.cristina.pep.jbcnconffeedback.activity

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.model.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

private val TAG = MainActivity::class.java.name
private const val SPEAKERS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"
private const val TALKS_URL = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var speakerDao: Dao<Speaker, Int>
    private lateinit var talkDao: Dao<Talk, Int>
    private lateinit var speakerTalkDao: Dao<SpeakerTalk, Int>
    private lateinit var requestQueue: RequestQueue

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
            //retrieveTalksFromWeb()

        } else {
            Toast.makeText(applicationContext, "There is no network connection try later.", Toast.LENGTH_LONG).show()
            // exitProcess(-1)
        }
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
        retrieveTalksFromWeb()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
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
}
