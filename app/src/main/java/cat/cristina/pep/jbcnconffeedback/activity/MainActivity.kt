package cat.cristina.pep.jbcnconffeedback.activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.model.UtilDAOImpl
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.net.ConnectivityManager
import cat.cristina.pep.jbcnconffeedback.model.DatabaseHelper
import cat.cristina.pep.jbcnconffeedback.model.Speaker
import cat.cristina.pep.jbcnconffeedback.model.Talk
import com.android.volley.RequestQueue
import com.google.gson.Gson
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import org.json.JSONObject


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var databaseHelper: DatabaseHelper? = null
    private var speakerDao: Dao<Speaker, Int>? = null
    private var talkDao: Dao<Talk, Int>? = null

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

        if (isDeviceConnectedToWifi()) {
            val queue = Volley.newRequestQueue(this)
            getDatabaseHelper()
            retrieveSpeakersFromWeb(queue)
            retrieveTalksFromWeb(queue)
        }
    }

    private fun getDatabaseHelper(): DatabaseHelper {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(applicationContext, DatabaseHelper::class.java)
        }
        return databaseHelper!!
    }

    private fun retrieveSpeakersFromWeb(queue: RequestQueue) {
        val urlSpeakers = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/speakers.json"

        val speakersRequest = JsonObjectRequest(Request.Method.GET, urlSpeakers, null,
                Response.Listener { response ->
                    Log.d(tag, "Speakers Response: %s".format(response.toString()))
                    parseSpeakers(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.d(tag, error.toString())
                })
        queue.add(speakersRequest)
    }

    private fun retrieveTalksFromWeb(queue: RequestQueue) {
        val urlTalks = "https://raw.githubusercontent.com/barcelonajug/jbcnconf_web/gh-pages/2018/_data/talks.json "

        val talksRequest = JsonObjectRequest(Request.Method.GET, urlTalks, null,
                Response.Listener { response ->
                    Log.d(tag, "Talks Response: %s".format(response.toString()))
                    parseTalks(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.d(tag, error.toString())
                })
        queue.add(talksRequest)
    }

    private fun parseTalks(talks: String) {
        val json: JSONObject = JSONObject(talks)
        val items = json.getJSONArray("items")
        talkDao = databaseHelper!!.getTalkDao()
        val gson: Gson = Gson()

        for(i in 0 until (items.length())) {
            var talkObject = items.getJSONObject(i)
            var talk = gson.fromJson(talkObject.toString(), Talk::class.java)
            talkDao!!.create(talk)
        }
        val dao = UtilDAOImpl(applicationContext)
        val talks = dao.lookupTalks()

        for (talk in talks)
            Log.d(tag, "ADIOS -> " + talk.toString())
    }

    private fun parseSpeakers(speakers: String) {
        val json: JSONObject = JSONObject(speakers)
        val items = json.getJSONArray("items")
        getDatabaseHelper()
        speakerDao = databaseHelper!!.getSpeakerDao()
        val gson: Gson = Gson()

        for (i in 0 until (items.length())) {
            var speakerObject = items.getJSONObject(i)
            var speaker = gson.fromJson(speakerObject.toString(), Speaker::class.java)
            speakerDao!!.create(speaker)
        }
    }

    private fun isDeviceConnectedToWifi(): Boolean {
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

    companion object {
        val tag = MainActivity::class.java.name
    }

    private fun parseSpeakers2(speakers: String) {
        val json: JSONObject = JSONObject(speakers)
        val items = json.getJSONArray("items")
        speakerDao = databaseHelper!!.getSpeakerDao()

        for (i in 0 until (items.length())) {
            var speakerObject = items.getJSONObject(i)
            var speaker = Speaker(
                    0,
                    speakerObject.get("enabled").toString(),
                    speakerObject.get("name").toString(),
                    speakerObject.get("description").toString(),
                    speakerObject.get("biography").toString(),
                    speakerObject.get("image").toString(),
                    speakerObject.get("url").toString(),
                    speakerObject.get("ref").toString(),
                    speakerObject.get("twitter").toString()
            )
            speakerDao!!.create(speaker)

        }
        val dao = UtilDAOImpl(applicationContext)
        val speakers = dao.lookupSpeakers()

        for (speaker in speakers)
            Log.d(tag, speaker.toString())
    }
}
