package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cat.cristina.pep.jbcnconffeedback.R
import kotlinx.android.synthetic.main.fragment_welcome.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_ROOM_NAME = "param1"
private const val ARG_NEXT_TALK = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WelcomeFragment.OnWelcomeFragmentListener] interface
 * to handle interaction events.
 * Use the [WelcomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WelcomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var roomName: String? = null
    private var nextTalk: String? = null
    private var listener: OnWelcomeFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomName = it.getString(ARG_ROOM_NAME)
            nextTalk = it.getString(ARG_NEXT_TALK)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_room_name.text = roomName
        tv_next_talk_name.text = nextTalk
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(msg: String) {
        listener?.onWelcomeFragment(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnWelcomeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnWelcomeFragmentListener")
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
    interface OnWelcomeFragmentListener {
        // TODO: Update argument type and name
        fun onWelcomeFragment(msg: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WelcomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                WelcomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_ROOM_NAME, param1)
                        putString(ARG_NEXT_TALK, param2)
                    }
                }
    }
}
