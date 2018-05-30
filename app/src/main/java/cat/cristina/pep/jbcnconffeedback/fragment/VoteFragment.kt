package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import cat.cristina.pep.jbcnconffeedback.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_vote.*


private const val ARG_TALK_ID = "talkId"
private const val ARG_TALK_TITLE = "talkTitle"
private const val ARG_SPEAKER_NAME = "speakerName"

/**
 *
 */

const val VOTE_FIVE = 5
const val VOTE_FOUR = 4
const val VOTE_THREE = 3
const val VOTE_TWO = 2
const val VOTE_ONE = 1

class VoteFragment : Fragment() {

    private val TAG = VoteFragment::class.java.name
    private lateinit var talkId: String
    private lateinit var talkTitle: String
    private lateinit var speakerName: String
    private var listener: OnVoteFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            talkId = it.getString(ARG_TALK_ID)
            talkTitle = it.getString(ARG_TALK_TITLE)
            speakerName = it.getString(ARG_SPEAKER_NAME)
        }
        setHasOptionsMenu(true)

        // activity?.toolbar?.setNavigationIcon(null)
        // activity?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTalkTitle.text = talkTitle
        tvSpeakerName.text = "By $speakerName"
        val onTouchAnimation = AnimationUtils.loadAnimation(activity, R.anim.click_animation)

        ibLove.setOnClickListener {
            ibLove.startAnimation(onTouchAnimation)
            onButtonPressed(talkId.toInt(), VOTE_FIVE)
        }
        ibSmile.setOnClickListener {
            ibSmile.startAnimation(onTouchAnimation)
            onButtonPressed(talkId.toInt(), VOTE_FOUR)
        }
        ibNormal.setOnClickListener {
            ibNormal.startAnimation(onTouchAnimation)
            onButtonPressed(talkId.toInt(), VOTE_THREE)
        }
        ibSleepy.setOnClickListener {
            ibSleepy.startAnimation(onTouchAnimation)
            onButtonPressed(talkId.toInt(), VOTE_TWO)
        }
        ibCry.setOnClickListener {
            ibCry.startAnimation(onTouchAnimation)
            onButtonPressed(talkId.toInt(), VOTE_ONE)
        }
        Log.d(TAG, "talkTitle -> " + talkTitle + ", speakerName -> " + speakerName)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.vote_fragment, menu)
    }

    /* TODO("retore ChooseTalkFragment")  */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_exit -> {

                val alertDialogBuilder = AlertDialog.Builder(context!!, R.style.Base_V7_Theme_AppCompat_Dialog)
                alertDialogBuilder.setTitle(R.string.alert_title)
                alertDialogBuilder.setMessage(R.string.alert_message)
                // TODO ("What of backstack empty as in automode")
                alertDialogBuilder.setPositiveButton(R.string.alert_positive_button) {
                    _, _ ->  fragmentManager?.popBackStack()
                }
                alertDialogBuilder.setNegativeButton(R.string.alert_negative_button) {
                    _, _ ->
                }
                alertDialogBuilder.create().show()

                return true

            }
        }
        return true
    }

    /*  */
    fun onButtonPressed(id_talk: Int, score: Int) {
        listener?.onVoteFragment(id_talk, score)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnVoteFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnVoteFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        // activity?.toolbar?.setNavigationIcon(R.drawable.hamburger_icon_white)
        // activity?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    /**
     * This method gets called every time a vote is casted
     */
    interface OnVoteFragmentListener {
        fun onVoteFragment(id_talk: Int, score: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param talkId Parameter 1.
         * @param talkTitle Parameter 2.
         * @param speakerName Parameter 3.
         * @return A new instance of fragment VoteFragment.
         */
        @JvmStatic
        fun newInstance(talkId: String, talkTitle: String, speakerName: String) =
                VoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TALK_ID, talkId)
                        putString(ARG_TALK_TITLE, talkTitle)
                        putString(ARG_SPEAKER_NAME, speakerName)
                    }
                }
    }
}
