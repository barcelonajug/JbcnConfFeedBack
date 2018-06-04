package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent
import cat.cristina.pep.jbcnconffeedback.fragment.provider.TalkContent.TalkItem
import cat.cristina.pep.jbcnconffeedback.utils.PreferenceKeys
import java.text.SimpleDateFormat
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ChooseTalkFragment.OnChooseTalkListener] interface.
 */
class ChooseTalkFragment : Fragment() {

    private val TAG = ChooseTalkFragment::class.java.name

    private lateinit var talkContent: TalkContent
    //private lateinit var databaseHelper: DatabaseHelper
    //private lateinit var utilDAOImpl: UtilDAOImpl
    // TODO: Customize parameters
    private var columnCount = 1
    private var isFiltered = false
    private lateinit var sharedPreferences: SharedPreferences
    private var listener: OnChooseTalkListener? = null
    private var dateStr: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            isFiltered = it.getBoolean(ARG_FILTER_BY_DATE)
            dateStr = it.getString(ARG_DATE)
        }
        //            val date = SimpleDateFormat("dd/MM/yyyy").parse(dateStr)
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = simpleDateFormat.parse(dateStr)
        val hour = GregorianCalendar().get(Calendar.HOUR_OF_DAY)
        val minutes = GregorianCalendar().get(Calendar.MINUTE)
        date.time = date.time + ((hour * 60 + minutes) * 60 * 1_000)
        talkContent = TalkContent(activity!!.applicationContext, date)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
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
                adapter =
                        if (isFiltered) MyTalkRecyclerViewAdapter(talkContent.ITEMS_FILTERED_BY_DATE_AND_ROOM_NAME, listener, context)
                        else MyTalkRecyclerViewAdapter(talkContent.ITEMS, listener, context)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var numTalks = 0
        numTalks = if (isFiltered) {
            talkContent.ITEMS_FILTERED_BY_DATE_AND_ROOM_NAME.size
        } else {
            talkContent.ITEMS.size
        }
        if (!sharedPreferences.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false))
            Toast.makeText(context, "$numTalks ${resources.getString(R.string.showing_n_talks)}", Toast.LENGTH_SHORT).show()
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
            R.id.action_filter -> {
                val roomName = sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name))
                if (roomName == resources.getString(R.string.pref_default_room_name)) {
                    Toast.makeText(context, resources.getString(R.string.sorry_no_room_set), Toast.LENGTH_SHORT).show()
                } else {
                    listener?.onFilterTalks(!isFiltered)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val itemFilter = menu?.findItem(R.id.action_filter)
        itemFilter?.title =
                resources.getString(if (isFiltered) R.string.filter_all else R.string.filter_apply)
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
        fun onChooseTalk(item: TalkItem?)
        fun onUpdateTalks()
        fun onFilterTalks(filtered: Boolean)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_FILTER_BY_DATE = "filter-by-day"
        const val ARG_DATE = "date"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int = 1, filterByDayAndRoomName: Boolean = false, dateStr: String = SimpleDateFormat("dd/MM/yyyy").format(Date())) =
                ChooseTalkFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                        putBoolean(ARG_FILTER_BY_DATE, filterByDayAndRoomName)
                        putString(ARG_DATE, dateStr)
                    }
                }
    }
}
