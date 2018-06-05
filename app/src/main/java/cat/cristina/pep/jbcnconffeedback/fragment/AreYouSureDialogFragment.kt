package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cat.cristina.pep.jbcnconffeedback.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AreYouSureDialogFragment.AreYouSureDialogFragmentListener] interface
 * to handle interaction events.
 * Use the [AreYouSureDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
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
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(resp: Int) {
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
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface AreYouSureDialogFragmentListener {
        fun onAreYouSureDialogFragmentInteraction(resp: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AreYouSureDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
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
