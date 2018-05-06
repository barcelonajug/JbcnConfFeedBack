package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import cat.cristina.pep.jbcnconffeedback.R

import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent.TalkItem
import kotlinx.android.synthetic.main.fragment_talk_list.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ChooseTalkFragment.OnChooseTalkListener] interface.
 */
class ChooseTalkFragment : Fragment() {

    private val TAG = ChooseTalkFragment::class.java.name

    private lateinit var talkContent: TalkContent

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnChooseTalkListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        talkContent = TalkContent(activity.applicationContext)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_talk_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyTalkRecyclerViewAdapter(talkContent.ITEMS, listener, context)
            }
        }
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnChooseTalkListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater?.inflate(R.menu.choose_talk_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_reload -> {
                Log.d(TAG, "reload")
                reload()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /* TODO("Review reload data: should connect to firebase") */
    private fun reload() {
        with(list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyTalkRecyclerViewAdapter(talkContent.ITEMS, listener, context)
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnChooseTalkListener {
        // TODO: Update argument type and name
        fun onChooseTalk(item: TalkItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                ChooseTalkFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
