package cat.cristina.pep.jbcnconffeedback

import android.os.Build.VERSION_CODES.LOLLIPOP
import cat.cristina.pep.jbcnconffeedback.model.*
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.junit.After


@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(LOLLIPOP), packageName = "cat.cristina.pep.jbcnconffeedback")
class UtilDAOImplTest {

    private lateinit var dao: UtilDAOImpl
    private lateinit var speakers: List<Speaker>
    private lateinit var talks: List<Talk>
    private var databaseHelper: DatabaseHelper = getDatabaseHelper()
    private lateinit var speakerDao: Dao<Speaker, Int>
    private lateinit var talkDao: Dao<Talk, Int>
    private lateinit var speakerTalkDao: Dao<SpeakerTalk, Int>

    @Before
    fun beforeEachTest(): Unit {
        dao = UtilDAOImpl(RuntimeEnvironment.application, databaseHelper)
        populate()
    }

    @After
    fun afterEachTest() {
        dao.onDestroy()
    }

    @Test
    fun checkThatTereAreTalks() {
        talks = dao.lookupTalks()
        Assert.assertTrue(talks.size > 0)
    }

    @Test
    fun checkThatTereAreSpeakers() {
        speakers = dao.lookupSpeakers()
        Assert.assertTrue(speakers.size > 0)
    }

    @Test
    fun checkLookupSpeakersByRef() {
        val michelSchudel = dao.lookupSpeakerByRef("TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==")
        Assert.assertEquals("Michel Schudel", michelSchudel.name)
    }

    @Test
    fun checkLookupSpeakersListForTalkIsNotEmpty() {
        talks = dao.lookupTalks()
        val speakers: List<Speaker> = dao.lookupSpeakersForTalk(talks.get(0))
        Assert.assertTrue(speakers.isNotEmpty())
    }

    @Test
    fun checkLookupSpeakersListForTalkHasCorrectSpeaker() {
        talks = dao.lookupTalks()
        val speakers: List<Speaker> = dao.lookupSpeakersForTalk(talks.get(1))
        Assert.assertEquals("Mercedes Wyss", speakers.get(0).name)
    }

    private fun getDatabaseHelper(): DatabaseHelper {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper::class.java)
        }
        return databaseHelper
    }

    private fun populate() {

        // ****************************** Speakers **************************
        speakerDao = databaseHelper.getSpeakerDao()
        talkDao = databaseHelper.getTalkDao()

        val speakersOfTalk1 = arrayOf("TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==")
        val talk1 = Talk(
                0,
                "Let's build a blockchain in 50 minutes!",
                "Blockchain technology is hot! But how does it actually work? I always found that the best way to get familiar with new technology is to build something with it. \\n\\nSo, let's build a blockchain, then!\\n\\nIn this live coding session, I will build a blockchain from scratch, using Java 8 with a little SpringBoot sprinkled over it. The talk will include all concepts of a blockchain, like transactions, blocks, immutability, proof-of-work, and consensus in the network. After this session you will have a better understanding of how blockchains work and how to implement them!",
                "talk",
                //tags,
                "middle",
                speakersOfTalk1
        )

        talkDao.create(talk1)

        val speaker1 = Speaker(
                0,
                "Michel Schudel",
                "Java developer Dutch Railways",
                "Michel Schudel has been a passionate Java developer since 1998, building various Java solutions for banks, insurance companies and telecom providers. Since then he has seen the good, the bad and the ugly in Java land. He loves agile development and coding with micro and meso frameworks like SpringBoot and SparkJava to get up-and-running as fast as possible. Futhermore, he likes to coach junior developers in Core Java. Michel is currently working for Dutch Railways in the Netherlands.",
                "assets/img/speakers/michel-schudel.jpg",
                "infoSpeaker.html?ref=TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==",
                "TWljaGVsU2NodWRlbG1pY2hlbC5zY2h1ZGVsQGdtYWlsLmNvbQ==",
                "@michelschudel"
        )
        speakerDao.create(speaker1)

        val speakersOfTalk2: Array<String> = arrayOf("TWVyY2VkZXNXeXNzbWVyY2VkZXMud3lzc0Bwb3dlcnd0ZWNobm9sb2d5LmNvbQ==")
        val talk2 = Talk(
                0,
                "Serverless in Deep",
                "Maybe You have been listening about Serverless. But, do you really know what is Serverless? If you don't have any knowledge, or want to delve into the subject, this is the talk for you. We will discuss what is Serverless, its principles, how it relates to FaaS (Functions as a Service), its benefits and drawbacks. But the most import its architecture in deep, the most relevant use cases or Design Patterns, which technologies and tools we have in the market for implement Serverless, and how they come to relate the Serverless with containers.",
                "talk",
                //tags2,
                "middle",
                speakersOfTalk2
        )
        talkDao.create(talk2)

        val speaker2 = Speaker(
                0,
                "Mercedes Wyss",
                "CTO at Produactivity",
                "She is a software engineer with more than six years of experience in backend, frontend, and Android development using Java and Kotlin. Currently, Mercedes is the CTO at Produactivity, a startup based in Guatemala.\\\\n\\\\nOutside of work, she was previously organizing meetings in Guatemala Java Users Group. Now she is focused on increasing women's participation in STEAM by running a JDuchess chapter in Guatemala and helping new communities to make their first steps. \\\\n\\\\nShe is a Developer Champion and an Auth0 Ambassador, has a Duke's Choice Award in Educational Outreach, and is the leader of a Google community, Devs+502.",
                "assets/img/speakers/mercedes-wyss.jpg",
                "infoSpeaker.html?ref=TWVyY2VkZXNXeXNzbWVyY2VkZXMud3lzc0Bwb3dlcnd0ZWNobm9sb2d5LmNvbQ==",
                "TWVyY2VkZXNXeXNzbWVyY2VkZXMud3lzc0Bwb3dlcnd0ZWNobm9sb2d5LmNvbQ==",
                "@itrjwyss"
        )
        speakerDao.create(speaker2)

        speakerTalkDao = databaseHelper.getSpeakerTalkDao()

        val speakerTalk1 = SpeakerTalk(
                0,
                speaker1,
                talk1
        )

        speakerTalkDao.create(speakerTalk1)

        val speakerTalk2 = SpeakerTalk(
                0,
                speaker2,
                talk2
        )
        speakerTalkDao.create(speakerTalk2)
    }

}