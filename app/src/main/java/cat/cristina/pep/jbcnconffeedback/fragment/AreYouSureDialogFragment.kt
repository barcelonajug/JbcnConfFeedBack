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

class AreYouSureDialogFragment : DialogFragment() {

    private val TAG = AreYouSureDialogFragment::class.java.name

    private var param1: String? = null
    private var param2: String? = null
    private var listener: AreYouSureDialogFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater

        val view = inflater.inflate(R.layout.fragment_are_you_sure_dialog, null)

        val dialog = builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, { dialog, id ->
                    onButtonPressed(Dialog.BUTTON_POSITIVE)
                })
                .setNegativeButton(R.string.cancel, { _, _ ->
                    onButtonPressed(Dialog.BUTTON_NEGATIVE)
                }).create()

        dialog.window.setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame)

        return dialog

    }

    private fun onButtonPressed(resp: Int) {
        listener?.onAreYouSureDialogFragmentInteraction(resp)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AreYouSureDialogFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement AreYouSureDialogFragmentListener")
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
    interface AreYouSureDialogFragmentListener {
        fun onAreYouSureDialogFragmentInteraction(resp: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
                AreYouSureDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
