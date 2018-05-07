package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import cat.cristina.pep.jbcnconffeedback.R
import kotlinx.android.synthetic.main.fragment_vote.*

private const val ARG_TALK_ID = "talkId"
private const val ARG_TALK_TITLE = "talkTitle"
private const val ARG_SPEAKER_NAME = "speakerName"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [VoteFragment.OnVoteFragmentListener] interface
 * to handle interaction events.
 * Use the [VoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTalkTitle.text = talkTitle
        tvSpeakerName.text = "By $speakerName"
        val onTouchAnimation = AnimationUtils.loadAnimation(activity, R.anim.click_animation)

        ibLove.setOnClickListener {
            // ibLove.setImageResource(R.drawable.love_selected)
            ibLove.startAnimation(onTouchAnimation)
            onButtonPressed(talkId!!.toInt(), 5)
        }
        ibSmile.setOnClickListener {
            ibSmile.startAnimation(onTouchAnimation)
            onButtonPressed(talkId!!.toInt(), 4)
        }
        ibNormal.setOnClickListener {
            ibNormal.startAnimation(onTouchAnimation)
            onButtonPressed(talkId!!.toInt(), 3)
        }
        ibSleepy.setOnClickListener {
            ibSleepy.startAnimation(onTouchAnimation)
            onButtonPressed(talkId!!.toInt(), 2)
        }
        ibCry.setOnClickListener {
            ibCry.startAnimation(onTouchAnimation)
            onButtonPressed(talkId!!.toInt(), 1)
        }
        Log.d(TAG, "talkTitle -> " + talkTitle + ", speakerName -> " + speakerName)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.vote_fragment, menu)
    }

    /* TODO("retore ChooseTalkFragment")  */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_exit -> {
                Log.d(TAG, "exit")
                fragmentManager.popBackStack()
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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnVoteFragmentListener {
        // TODO: Update argument type and name
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
