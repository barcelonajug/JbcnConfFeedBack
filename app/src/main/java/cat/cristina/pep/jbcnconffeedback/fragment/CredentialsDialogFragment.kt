package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CredentialsDialogFragment.CredentialsDialogFragmentListener] interface
 * to handle interaction events.
 * Use the [CredentialsDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * onAttach
 * onCreate
 * onCreateDialog
 * onCreateView
 * onActivityCreated
 * onStart
 * onResume
 *
 */
class CredentialsDialogFragment : DialogFragment() {
    /**
     * Called when an action is being performed.
     *
     * @param v The view that was clicked.
     * @param actionId Identifier of the action.  This will be either the
     * identifier you supplied, or [ EditorInfo.IME_NULL][EditorInfo.IME_NULL] if being called due to the enter key
     * being pressed.
     * @param event If triggered by an enter key, this is the event;
     * otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listenerDialog: CredentialsDialogFragmentListener? = null

    private lateinit var usernameET: AppCompatAutoCompleteTextView
    private lateinit var passwordET: EditText

    private val USERNAMES = arrayOf("nacho@barcelonajug.org", "jonathan@barcelonajug.org", "cristina.asensio.munoz@gmail.com", "jmendez1@xtec.cat")


    private val TAG = CredentialsDialogFragment::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog")
        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val view = inflater.inflate(R.layout.fragment_credentials_dialog, null)

        usernameET = view.findViewById(R.id.username)
        passwordET = view.findViewById(R.id.password)

        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, USERNAMES)

        usernameET.setAdapter(arrayAdapter)

        return builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.sign_in, DialogInterface.OnClickListener { dialog, id ->
                    onButtonPressed(Dialog.BUTTON_POSITIVE)
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
                    onButtonPressed(Dialog.BUTTON_NEGATIVE)
                    //this@CredentialsDialogFragment.dialog.cancel()
                }).create()

    }


    private fun onButtonPressed(answer: Int) {

        val userName = usernameET.text.toString()
        val passWord = passwordET.text.toString()

        /* param1 es desde donde se llama  */
        if (answer == Dialog.BUTTON_NEGATIVE) {
            listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
        } else if (userName.isEmpty() || passWord.isEmpty()) {
            Toast.makeText(activity, R.string.blank_credentials, Toast.LENGTH_LONG).show()
            listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
        } else if (param1 == "MainActivity") {
            if (answer == Dialog.BUTTON_POSITIVE) {
                if (userName == passWord) {
                    /* Do nothing  */
                    listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_POSITIVE)
                } else {
                    Toast.makeText(activity, R.string.wrong_credentials, Toast.LENGTH_LONG).show()
                    listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
                }
            }
        } else if (param1 == "VoteFragment") {
            if (answer == Dialog.BUTTON_POSITIVE) {
                if (userName == passWord) {
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(activity, R.string.wrong_credentials, Toast.LENGTH_LONG).show()
                    listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
                }
            }
        }
    }


    /*
    * Called when the user presses de back button to cancel the dialog
    * */
    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        // Toast.makeText(activity, "onCancel", Toast.LENGTH_LONG).show()
        listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CredentialsDialogFragmentListener) {
            listenerDialog = context
        } else {
            throw RuntimeException(context.toString() + " must implement CredentialsDialogFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerDialog = null
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
    interface CredentialsDialogFragmentListener {
        fun onCredentialsDialogFragmentInteraction(answer: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CredentialsDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CredentialsDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
