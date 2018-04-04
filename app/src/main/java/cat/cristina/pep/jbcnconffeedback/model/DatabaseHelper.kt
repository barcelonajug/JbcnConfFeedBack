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
    private var speakerTalkDao: Dao<SpeakerTalk, Int>? = null

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        Log.i(DatabaseHelper::class.java.name, "in onCreate")

        try {
            TableUtils.createTable(connectionSource, Speaker::class.java)
            TableUtils.createTable(connectionSource, Talk::class.java)
            TableUtils.createTable(connectionSource, SpeakerTalk::class.java)

            populate()
        } catch (e: Exception) {
            Log.e(DatabaseHelper::class.java.name, e.toString())
        }
    }

    private fun populate() {

        // ****************************** Speakers **************************
        speakerDao = getSpeakerDao()

        val speaker1 = Speaker(
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

        speakerDao!!.create(speaker1)

        val speaker2 = Speaker(
                0,
                1,
                "Mercedes Wyss",
                "CTO at Produactivity",
                "She is a software engineer with more than six years of experience in backend, frontend, and Android development using Java and Kotlin. Currently, Mercedes is the CTO at Produactivity, a startup based in Guatemala.\\\\n\\\\nOutside of work, she was previously organizing meetings in Guatemala Java Users Group. Now she is focused on increasing women's participation in STEAM by running a JDuchess chapter in Guatemala and helping new communities to make their first steps. \\\\n\\\\nShe is a Developer Champion and an Auth0 Ambassador, has a Duke's Choice Award in Educational Outreach, and is the leader of a Google community, Devs+502.",
                "assets/img/speakers/mercedes-wyss.jpg",
                "infoSpeaker.html?ref=TWVyY2VkZXNXeXNzbWVyY2VkZXMud3lzc0Bwb3dlcnd0ZWNobm9sb2d5LmNvbQ==",
                "TWVyY2VkZXNXeXNzbWVyY2VkZXMud3lzc0Bwb3dlcnd0ZWNobm9sb2d5LmNvbQ==",
                "@itrjwyss",
                "https://medium.com/@itrjwyss",
                2
        )
        speakerDao!!.create(speaker2)

        // ****************************** Talks **************************

        talkDao = getTalkDao()

        val tags = arrayOf("BlockChain", "Java", "Spring Boot")
        val tags2 = arrayOf("Serverless", "Kubernets", "Cloud","FaaS")

        val talk1 = Talk(
                0,
                "Let's build a blockchain in 50 minutes!",
                "Blockchain technology is hot! But how does it actually work? I always found that the best way to get familiar with new technology is to build something with it. \\n\\nSo, let's build a blockchain, then!\\n\\nIn this live coding session, I will build a blockchain from scratch, using Java 8 with a little SpringBoot sprinkled over it. The talk will include all concepts of a blockchain, like transactions, blocks, immutability, proof-of-work, and consensus in the network. After this session you will have a better understanding of how blockchains work and how to implement them!",
                "talk",
                tags,
                2)
        talkDao!!.create(talk1)

        val talk2 = Talk(
                0,
                "Serverless in Deep",
                "Maybe You have been listening about Serverless. But, do you really know what is Serverless? If you don't have any knowledge, or want to delve into the subject, this is the talk for you. We will discuss what is Serverless, its principles, how it relates to FaaS (Functions as a Service), its benefits and drawbacks. But the most import its architecture in deep, the most relevant use cases or Design Patterns, which technologies and tools we have in the market for implement Serverless, and how they come to relate the Serverless with containers.",
                "talk",
                tags2,
                2
        )
        talkDao!!.create(talk2)

        // ****************************** Speakers - Talk Relation **************************

        speakerTalkDao = getSpeakerTalkDao()

        val speakerTalk1 = SpeakerTalk(
                0,
                speaker1,
                talk1
        )

        speakerTalkDao!!.create(speakerTalk1)

        val speakerTalk2 = SpeakerTalk(
                0,
                speaker2,
                talk2
        )
        speakerTalkDao!!.create(speakerTalk2)
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

    internal fun getSpeakerTalkDao(): Dao<SpeakerTalk, Int> {
        return if (speakerTalkDao == null) {
            getDao(SpeakerTalk::class.java)
        } else speakerTalkDao!!
    }

    companion object {
        private val DATABASE_NAME = "db_feedback.sql"
        private val DATABASE_VERSION = 1
    }
}