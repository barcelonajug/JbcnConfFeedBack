package cat.cristina.pep.jbcnconffeedback

import android.os.Build.VERSION_CODES.LOLLIPOP
import cat.cristina.pep.jbcnconffeedback.model.Speaker
import cat.cristina.pep.jbcnconffeedback.model.Talk
import cat.cristina.pep.jbcnconffeedback.model.UtilDAOImpl
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

    private var dao: UtilDAOImpl? = null
    private var speakers: List<Speaker>? = null
    private var talks: List<Talk>? = null


    @Before
    fun beforeEachTest(): Unit {
        dao = UtilDAOImpl(RuntimeEnvironment.application)
        speakers = dao!!.lookupSpeakers()
        talks = dao!!.lookupTalks()
    }

    @After
    fun afterEachTest() {
        dao!!.onDestroy()
    }

    @Test
    fun checkThatThereAreTwoSpeakers() {
        Assert.assertEquals(2, speakers!!.size)
    }

    @Test
    fun checkThatThereAreTwoTalks() {
        Assert.assertEquals(2, talks!!.size)
    }

    @Test
    fun checkSecondSpeakerName() {
        val secondSpeakerName = speakers!![1].name
        Assert.assertEquals("Mercedes Wyss", secondSpeakerName)
    }

    @Test
    fun checkSpeakersForTalk() {
        val speakerForSecondTalk = dao!!.lookupSpeakersForTalk(talks!![1])
        Assert.assertEquals("Mercedes Wyss", speakerForSecondTalk[0].name)
    }
}