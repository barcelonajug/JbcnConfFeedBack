package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cat.cristina.pep.jbcnconffeedback.R
//import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.fragment_statistics.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val TAG = StatisticsFragment::class.java.name

/**
 */
class StatisticsFragment : Fragment() {

    private val TAG = StatisticsFragment::class.java.name

    private var param1: String? = null
    private var param2: String? = null
    private var listenerStatistics: OnStatisticsFragmentListener? = null
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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadScoring()
    }

    private fun setupGraph() {
        val labels = ArrayList<String>()
        val entries = ArrayList<BarEntry>()

        data
                ?.asSequence()
                ?.sortedBy {
                    it.key
                }
                ?.forEach {
                    labels.add("Talk ${it.key}")
                    val avg: Double? = data?.get(it.key)
                            ?.asSequence()
                            ?.map { doc ->
                                doc.get("score") as Long
                            }?.average()
                    entries.add(BarEntry(it.key!!.toFloat(), avg!!.toFloat()))
                }

        val barDataSet: BarDataSet = BarDataSet(entries, "Score")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()
        //barDataSet.barBorderColor = Color.BLACK

        val barData: BarData = BarData(barDataSet)
        // barData.dataSetLabels = labels

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.setDrawLabels(true)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.labelCount = labels.size
        barChart.fitScreen()
        // barChart.description = Description()
        barChart.setDrawBarShadow(true)
        barChart.setDrawValueAboveBar(true)
        //barChart.setFitBars(true)
        barChart.setBorderColor(Color.BLACK)
        barChart.setTouchEnabled(true)
        barChart.animateY(1_000)
        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }

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

    /* This method downloads the Scoring collection made up of documents(id_talk, score, date) */
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
                        /*
                        for (document in it.result) {
                            Log.d(TAG, "${document.id} -> ${document.data}")
                        }
                        */
                        setupGraph()
                    } else {
                        Log.d(TAG, "*** Error *** ${it.exception?.message}")
                    }
                }
    }

    /**
     */
    interface OnStatisticsFragmentListener {
        fun onStatisticsFragment(msg: String)
    }

    companion object {
        /**
         */
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
