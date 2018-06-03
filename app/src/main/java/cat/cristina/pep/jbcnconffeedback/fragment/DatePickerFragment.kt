package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ORIGIN = "origin"
private const val NOT_USED = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DatePickerFragment.DatePickerFragmentListener] interface
 * to handle interaction events.
 * Use the [DatePickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DatePickerFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var origin: String? = null
    private var notUsed: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            origin = it.getString(ORIGIN)
            notUsed = it.getString(NOT_USED)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker =
                fragmentManager?.findFragmentByTag(origin) as DatePickerDialog.OnDateSetListener
        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, datePicker, year, month, day)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param origin Parameter 1.
         * @param notUsed Parameter 2.
         * @return A new instance of fragment DatePickerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(origin: String? = null, notUsed: String? = null) =
                DatePickerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ORIGIN, origin)
                        putString(NOT_USED, notUsed)
                    }
                }
    }
}
