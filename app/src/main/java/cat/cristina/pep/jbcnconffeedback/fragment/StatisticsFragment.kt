package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cat.cristina.pep.jbcnconffeedback.R
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.charts.BarChart
import com.google.firebase.firestore.QueryDocumentSnapshot

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val TAG = StatisticsFragment::class.java.name

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StatisticsFragment.OnStatisticsFragmentListener] interface
 * to handle interaction events.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class StatisticsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var param1: String? = null
    private var param2: String? = null
    private var listenerStatistics: OnStatisticsFragmentListener? = null
    private var barChart: BarChart? = null
    private var data: Map<Long?, List<QueryDocumentSnapshot>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(msg: String) {
        listenerStatistics?.onStatisticsFragment(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnStatisticsFragmentListener) {
            listenerStatistics = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnStatisticsFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerStatistics = null
    }

    /*
    * This method downloads the Scoring collection made up of documents(id_talk, score, date)
    *
    * */
    private fun downloadScoring(): Unit {
        val firestore = FirebaseFirestore.getInstance()
        val scoring = firestore
                .collection("Scoring")
                //.whereEqualTo("score", 5)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        data = it.result.groupBy {
                            it.getLong("id_talk")
                        }

//                        for (document in it.result) {
//                            Log.d(TAG, "${document.id} -> ${document.data}")
//                        }

                    } else {
                        Log.d(TAG, "*** Error *** ${it.exception?.message}")
                    }
                }
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
    interface OnStatisticsFragmentListener {
        // TODO: Update argument type and name
        fun onStatisticsFragment(msg: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StatisticsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String = "param1", param2: String = "param2") =
                StatisticsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
