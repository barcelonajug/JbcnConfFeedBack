package cat.cristina.pep.jbcnconffeedback

import android.os.Build.VERSION_CODES.LOLLIPOP
import cat.cristina.pep.jbcnconffeedback.model.UtilDAOImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(LOLLIPOP), packageName = "cat.cristina.pep.jbcnconffeedback")
class UtilDAOImplTest {

    private var dao: UtilDAOImpl? = null

    @Before
    fun init() {
        dao = UtilDAOImpl(RuntimeEnvironment.application)
    }

    @Test
    fun checkThatThereAreTwoSpeakers() {
        val speakers = dao!!.lookupSpeakers()
        Assert.assertEquals(2, speakers.size)
    }

    @Test
    fun checkThatThereAreTwoTalks() {
        val talks = dao!!.lookupTalks()
        Assert.assertEquals(2, talks.size)
    }

    @Test
    fun checkSecondSpeakerName() {
        val speakers = dao!!.lookupSpeakers()
        val secondSpeakerName = speakers[1].name
        Assert.assertEquals("Mercedes Wyss", secondSpeakerName)
    }

    @Test
    fun checkSpeakersForTalk() {
        val talks = dao!!.lookupTalks()
        val speakerForSecondTalk = dao!!.lookupSpeakersForTalk(talks[1])
        Assert.assertEquals("Mercedes Wyss", speakerForSecondTalk[0].name)
    }
}