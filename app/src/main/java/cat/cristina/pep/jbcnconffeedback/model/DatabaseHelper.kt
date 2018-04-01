package cat.cristina.pep.jbcnconffeedback.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

data class DatabaseHelper(val context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var speakerDao: Dao<Speaker, Int>? = null
    private var talkDao: Dao<Talk, Int>? = null

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        Log.i(DatabaseHelper::class.java.name, "in onCreate")

        try {
            TableUtils.createTable(connectionSource, Speaker::class.java)
            TableUtils.createTable(connectionSource, Talk::class.java)
            populateSpeakers()
            populateTalks()
        } catch (e: Exception) {
            Log.e(DatabaseHelper::class.java.name, e.toString())
        }
    }

    private fun populateTalks() {
        talkDao = getTalkDao()

        val tags = arrayOf("Service Mesh", "Istio", "Kuberentes", "Microservices")

        var talk = Talk(
                0,
                "The next evolution of microservices patterns with Istio Mesh",
                "With microservices architectures, we're making more network calls and need to do more integration to get our system to work; this creates more ways for our applications to break and cause failures to propagate much faster. We need a way to call our microservices and be resilient to distributed systems failures â€” as a first-class implementation.\\n\\nPrevious generations of tools solving these problems were built at Twitter or Netflix but did so with application libraries. However, for each combination of platform/language/framework we use to build microservices we need to consistently solve for the following critical functions\\n\\n* routing / traffic shaping\\n* adaptive/clientside loadbalancing\\n* service discovery\\n* circuit breaking\\n* timeouts / retries \\n* rate limiting\\n* metrics/logging/tracing\\n* fault injection\\n\\nDoing all of these things in application-layer libraries across all your languages and all your frameworks becomes incredibly complex and expensive to maintain. In this talk, I'll introduce you to Envoy Proxy and Istio.io Service Mesh, discuss how they solve these problems more elegantly by pushing these concerns down to the infrastructure layer, and show demos of how it all works. ",
                "talk",
                tags,
                2)
        talkDao!!.create(talk)
    }

    private fun populateSpeakers() {
        speakerDao = getSpeakerDao()

        var speaker = Speaker(
                0,
                1,
                "Michel Schudel",
                "Java developer Dutch Railways",
                "Michel Schudel has been a passionate Java developer since 1998, building various Java solutions for banks, insurance companies and telecom providers. Since then he has seen the good, the bad and the ugly in Java land. He loves agile development and coding with micro and meso frameworks like SpringBoot and SparkJava to get up-and-running as fast as possible. Futhermore, he likes to coach junior developers in Core Java. Michel is currently working for Dutch Railways in the Netherlands.",
                "assets/img/speakers/michel-schudel.jpg",
                "infoSpeaker.html?ref=TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==",
                "TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==",
                "@michelschudel",
                "http://coding.michelschudel.nl/",
                1)

        speakerDao!!.create(speaker)

        speaker = Speaker(
                0,
                1,
                "Cristina Asensio",
                "Kotlin for Android developers",
                "una vida larga",
                "assets/img/speakers/michel-schudel.jpg",
                "infoSpeaker.html?ref=TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==",
                "TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==22",
                "@cristina",
                "http://coding.michelschudel.nl/",
                2
        )
        speakerDao!!.create(speaker)
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?, oldVersion: Int, newVersion: Int) {
        try {
            Log.i(DatabaseHelper::class.java.name, "onUpgrade")

            // Drop older db if existed
            database?.execSQL("DROP DATABASE " + DATABASE_NAME);

            // Create tables again
            onCreate(database)

        } catch (e: Exception) {
            Log.i(DatabaseHelper::class.java.name, e.message)
            throw RuntimeException(e)
        }
    }

    internal fun getSpeakerDao(): Dao<Speaker, Int> {
        return if (speakerDao == null) {
            getDao(Speaker::class.java)
        } else speakerDao!!
    }

    internal fun getTalkDao(): Dao<Talk, Int> {
        return if (talkDao == null) {
            getDao(Talk::class.java)
        } else talkDao!!
    }

    companion object {
        private val DATABASE_NAME = "db_feedback.sql"
        private val DATABASE_VERSION = 1
    }
}