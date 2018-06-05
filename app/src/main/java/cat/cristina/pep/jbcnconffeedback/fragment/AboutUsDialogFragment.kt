package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import cat.cristina.pep.jbcnconffeedback.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *
 */
class AboutUsDialogFragment : DialogFragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: AboutUsDialogFragmentListener? = null

    private val TAG = AboutUsDialogFragment::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val view = inflater.inflate(R.layout.fragment_about_us_dialog, null)

        val dialog = builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, { dialog, id ->
                    onButtonPressed("")
                }).create()

        dialog.window.setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame)

        return dialog

    }


    private fun onButtonPressed(msg: String) {
        listener?.onAboutUsDialogFragmentInteraction(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AboutUsDialogFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement AboutUsDialogFragmentListener")
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
     */
    interface AboutUsDialogFragmentListener {
        fun onAboutUsDialogFragmentInteraction(msg: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
                AboutUsDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
