package cat.cristina.pep.jbcnconffeedback.fragment

//import com.github.mikephil.charting.components.Description
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.activity.MainActivity
import cat.cristina.pep.jbcnconffeedback.model.DatabaseHelper
import cat.cristina.pep.jbcnconffeedback.model.Talk
import cat.cristina.pep.jbcnconffeedback.model.UtilDAOImpl
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.dao.Dao
import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.io.File
import java.io.FileWriter
import java.util.*
import java.util.stream.Collectors

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val TAG = StatisticsFragment::class.java.name

/**
 */
class StatisticsFragment : Fragment(), OnChartGestureListener {

    private val TAG = StatisticsFragment::class.java.name

    private var param1: String? = null
    private var param2: String? = null
    private var listenerStatistics: OnStatisticsFragmentListener? = null
    private var dataFromFirestore: Map<Long?, List<QueryDocumentSnapshot>>? = null
    private lateinit var dialog: ProgressDialog
    private lateinit var databaseHelper: DatabaseHelper
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
        databaseHelper = OpenHelperManager.getHelper(activity, DatabaseHelper::class.java)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadScoring()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater?.inflate(R.menu.statistics_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId) {
//            R.id.action_send_statistics -> {
//                createCVSFromStatistics(DEFAULT_STATISTICS_FILE_NAME)
//                sendCSVByEmail(DEFAULT_STATISTICS_FILE_NAME)
//            }
//
//        }
        return true
    }

    /* This method downloads the Scoring collection made up of documents(id_talk, score, date) */
    private fun downloadScoring(): Unit {

        if (!(context as MainActivity).isDeviceConnectedToWifiOrData().first) {
            Toast.makeText(context, R.string.sorry_no_graphic_available, Toast.LENGTH_LONG).show()
            return
        }

        dialog = ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setMessage(resources.getString(R.string.loading))
        dialog.isIndeterminate = true
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        FirebaseFirestore.getInstance()
                .collection("Scoring")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        dataFromFirestore = it.result.groupBy {
                            it.getLong("id_talk")
                        }
                        setupGraphTopNTalks(10L)
                    } else {
                        dialog.dismiss()
                        Toast.makeText(context, R.string.sorry_no_graphic_available, Toast.LENGTH_LONG).show()
                        //Log.d(TAG, "*** Error *** ${it.exception?.message}")
                    }
                }
    }

    /*
    * Not used
    *
    * */
    private fun setupGraph() {
        dialog.setMessage(resources.getString(R.string.processing));
        val labels = ArrayList<String>()
        val entries = ArrayList<BarEntry>()
        var i = 0.0F

        dataFromFirestore
                ?.asSequence()
                ?.sortedBy {
                    it.key
                }
                ?.forEach {
                    labels.add("Talk jhgkg kgkhg hkghg #${it.key}")
                    val avg: Double? = dataFromFirestore?.get(it.key)
                            ?.asSequence()
                            ?.map { doc ->
                                doc.get("score") as Long
                            }?.average()
                    entries.add(BarEntry(i++, avg!!.toFloat()))
                }

        val barDataSet: BarDataSet = BarDataSet(entries, "Score")

        with(barDataSet) {
            colors = ColorTemplate.COLORFUL_COLORS.asList()
            barBorderColor = Color.BLACK
        }

        val barData: BarData = BarData(barDataSet)

        with(barChart) {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = labels.size
            fitScreen()
            description.isEnabled = false
            setDrawBarShadow(true)
            setDrawValueAboveBar(true)
            //setFitBars(true)
            setBorderColor(Color.BLACK)
            setTouchEnabled(true)
            onChartGestureListener = this@StatisticsFragment
            animateY(1_000)
            legend.isEnabled = false
            legend.textColor = Color.GRAY
            legend.textSize = 15F

            notifyDataSetChanged()
            invalidate()
        }

        dialog.dismiss()

    }

    private fun setupGraphTopNTalks(limit: Long) {
        dialog.setMessage(resources.getString(R.string.processing))
        // Pair<title, avg>
        val titleAndAvg = ArrayList<Pair<String, Double>>()
        val labels = ArrayList<String>()
        val entries = ArrayList<BarEntry>()
        var index = 0.0F
        val talkDao: Dao<Talk, Int> = databaseHelper.getTalkDao()
        val utilDAOImpl = UtilDAOImpl(context!!, databaseHelper)

        dataFromFirestore
                ?.asSequence()
                ?.sortedBy {
                    it.key
                }
                ?.forEach {
                    val votes = it.value.size
                    val avg: Double? = dataFromFirestore?.get(it.key)
                            ?.asSequence()
                            ?.map { doc ->
                                doc.get("score") as Long
                            }?.average()
                    var title: String = talkDao.queryForId(it.key?.toInt()).title
                    var refAuthor = talkDao.queryForId(it.key?.toInt()).speakers?.get(0)
                    var author = utilDAOImpl.lookupSpeakerByRef(refAuthor!!).name
                    /* Si el nombre es demasiado largo se saldra de la barra  */
                    author = author.substring(0, 1) + "." + author.substring(author.indexOf(" "))
                    title = if (title.length > 35) "'${StringBuilder(title.substring(0, 35)).toString()}...' by $author. ($votes votes)"
                    else "'$title' by $author. ($votes votes)"
                    titleAndAvg.add(Pair(title, avg!!))
                    // Log.d(TAG, "************************************ $title $avg")
                }

        val firstTen = titleAndAvg
                .stream()
                .sorted { pair1, pair2 -> if (pair1.second < pair2.second) 1 else if (pair1.second == pair2.second) 0 else -1 }
                .limit(limit)
                .collect(Collectors.toList())

        for (pair: Pair<String, Double> in firstTen) {
            entries.add(BarEntry(index++, pair.second.toFloat()))
            var title: String = pair.first
            //title = StringBuilder(title).append(" (${String.format("%.2f", pair.second)})").toString()
            labels.add(title)
        }

        val barDataSet: BarDataSet = BarDataSet(entries, "Score")

        with(barDataSet) {
            colors = ColorTemplate.COLORFUL_COLORS.asList()
            barBorderColor = Color.BLACK
        }

        val barData = BarData(barDataSet)
        barData.setDrawValues(true)
        barData.setValueTextColor(Color.BLACK)
        barData.setValueTextSize(24.0F)

        with(barChart) {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels) as IAxisValueFormatter?
            xAxis.textSize = 20.0F
            xAxis.setDrawLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            xAxis.xOffset = 650.0F
            xAxis.yOffset = 100.0F
            xAxis.setLabelCount(firstTen!!.size, false)
            axisLeft.axisMinimum = 0.0F
            axisLeft.axisMaximum = 5.25F
            axisLeft.setDrawLabels(false)
            axisRight.setDrawLabels(false)
            fitScreen()
            description.isEnabled = false
            //description.text = resources.getString(R.string.chart_description)
            setNoDataText(resources.getString(R.string.sorry_no_graphic_available))
            setDrawBarShadow(true)
            //setDrawValueAboveBar(true)
            //setFitBars(true)
            setDrawBorders(true)
            setBorderColor(Color.BLACK)
            setTouchEnabled(true)
            // onChartGestureListener = this@StatisticsFragment
            animateXY(2_500, 5_000)
            legend.isEnabled = false
//            legend.textColor = Color.GRAY
//            legend.textSize = 15F

            notifyDataSetChanged()
            invalidate()
        }

        dialog.dismiss()
    }

    /*
    * No usar.
    * CSV From list of objects
    * No va, tiene una depedencia externa y da error missing "java.beans.Introspector"
    * https://code.google.com/archive/p/openbeans/downloads
    *
    * Lo añado al proyecto como dependencia externa pero sigue sin ir y cuando
    * hago un rebuild del proyecto elimina el directorio libs y el contenido
    * */
    private fun createCVSFromStatisticsByObject(fileName: String): Unit {
        data class Statistic(val id: Long, val title: String, val score: Int, val date: Date)

        val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val fileWriter = FileWriter(file)
        val statistics = arrayListOf<Statistic>()

        dataFromFirestore
                ?.asSequence()
                ?.forEach {
                    dataFromFirestore?.get(it.key)
                            ?.asSequence()
                            ?.map { doc ->
                                val id = doc.getLong("id_talk")
                                val title = databaseHelper.getTalkDao().queryForId(id?.toInt()).title
                                val score = doc.get("score") as Int
                                val date = doc.getDate("date") as Date
                                statistics.add(Statistic(id!!, title, score, date))
                            }
                }

        val mappingStrategy =
                ColumnPositionMappingStrategy<Statistic>()
                        .apply {
                            type = Statistic::class.java
                            setColumnMapping("id_talk", "title", "score", "date")
                        }

        var beanToCsv = StatefulBeanToCsvBuilder<Statistic>(fileWriter)
                .withMappingStrategy(mappingStrategy)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                .build()

        beanToCsv.write(statistics)
        Log.d(TAG, fileWriter.toString())
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

    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me
     * @param lastPerformedGesture
     */
    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
    }

    /**
     * Callbacks then a fling gesture is made on the chart.
     *
     * @param me1
     * @param me2
     * @param velocityX
     * @param velocityY
     */
    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
    }

    /**
     * Callbacks when the chart is single-tapped.
     *
     * @param me
     */
    override fun onChartSingleTapped(me: MotionEvent?) {
        Log.d(TAG, "singledTap ${me.toString()}")

    }

    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     *
     * @param me
     * @param lastPerformedGesture
     */
    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
    }

    /**
     * Callbacks when the chart is scaled / zoomed via pinch zoom gesture.
     *
     * @param me
     * @param scaleX scalefactor on the x-axis
     * @param scaleY scalefactor on the y-axis
     */
    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
    }

    /**
     * Callbacks when the chart is longpressed.
     *
     * @param me
     */
    override fun onChartLongPressed(me: MotionEvent?) {
    }

    /**
     * Callbacks when the chart is double-tapped.
     *
     * @param me
     */
    override fun onChartDoubleTapped(me: MotionEvent?) {
    }

    /**
     * Callbacks when the chart is moved / translated via drag gesture.
     *
     * @param me
     * @param dX translation distance on the x-axis
     * @param dY translation distance on the y-axis
     */
    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
    }
}
