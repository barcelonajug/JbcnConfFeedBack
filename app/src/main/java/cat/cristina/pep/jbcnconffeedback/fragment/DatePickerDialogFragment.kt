package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.DatePicker
import cat.cristina.pep.jbcnconffeedback.activity.MainActivity.Companion.simpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *
 */
class DatePickerDialogFragment : DialogFragment(),  DatePickerDialog.OnDateSetListener {

    /* dateSet esta en format dd/MM/yyyy  */
    private var dateSet: String? = null
    private var param2: String? = null
    private var listener: DatePickerDialogFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateSet = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val date = simpleDateFormat.parse(dateSet)
        val c = GregorianCalendar()
        c.time = date
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity, this, year, month, day)
    }

    fun onButtonPressed(msg: String) {
        listener?.onDatePikerDialogFragmentInteraction(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DatePickerDialogFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement DatePickerDialogFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     *
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = GregorianCalendar(year, month, dayOfMonth).time
        onButtonPressed(simpleDateFormat.format(date))
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface DatePickerDialogFragmentListener {
        fun onDatePikerDialogFragmentInteraction(msg: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
                DatePickerDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
