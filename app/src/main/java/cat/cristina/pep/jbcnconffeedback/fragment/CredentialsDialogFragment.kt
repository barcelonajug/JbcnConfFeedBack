package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.activity.MainActivity


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * As a reminder:
 *
 * onAttach, onCreate, onCreateDialog, onCreateView, onActivityCreated, onStart, onResume
 *
 */
class CredentialsDialogFragment : DialogFragment() {

    private var origin: String? = null
    private var autoMode: Boolean? = null
    private var listenerDialog: CredentialsDialogFragmentListener? = null

    private lateinit var usernameET: AppCompatAutoCompleteTextView
    private lateinit var passwordET: EditText

    private val USERNAMES = arrayOf("nacho@barcelonajug.org", "jonathan@barcelonajug.org", "cristina.asensio.munoz@gmail.com", "jmendez1@xtec.cat")

    private val TAG = CredentialsDialogFragment::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            origin = it.getString(ARG_PARAM1)
            autoMode = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.fragment_credentials_dialog, null)

        usernameET = view.findViewById(R.id.username)
        passwordET = view.findViewById(R.id.password)

        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, USERNAMES)

        usernameET.setAdapter(arrayAdapter)

        val dialog = builder.setView(view)
                .setPositiveButton(R.string.sign_in, { _, _ ->
                    onButtonPressed(Dialog.BUTTON_POSITIVE)
                })
                .setNegativeButton(R.string.cancel, { dialog, id ->
                    onButtonPressed(Dialog.BUTTON_NEGATIVE)
                    //this@CredentialsDialogFragment.dialog.cancel()
                }).create()

        dialog.window.setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame)

        return dialog

    }

    private fun onButtonPressed(answer: Int) {

        val userName = usernameET.text.toString()
        val passWord = passwordET.text.toString()

        if (answer == Dialog.BUTTON_NEGATIVE) {
            listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
        } else if (userName.isEmpty() || passWord.isEmpty()) {
            Toast.makeText(activity, R.string.blank_credentials, Toast.LENGTH_LONG).show()
            listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
        } else if (origin == MainActivity.MAIN_ACTIVITY) {  /* origin es desde donde se llama  */
            if (answer == Dialog.BUTTON_POSITIVE) {
                //if (userName == passWord) {
                if (resources.getString(R.string.secret_password) == passWord) {
                    /* Do nothing  */
                    listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_POSITIVE)
                } else {
                    Toast.makeText(activity, R.string.wrong_credentials, Toast.LENGTH_LONG).show()
                    listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
                }
            }
        } else if (origin == MainActivity.VOTE_FRAGMENT) {
            if (autoMode == false) {
                if (answer == Dialog.BUTTON_POSITIVE) {
                    //if (userName == passWord) {
                    if (resources.getString(R.string.secret_password) == passWord) {
                        fragmentManager?.popBackStack()
                        listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_POSITIVE)
                    } else {
                        Toast.makeText(activity, R.string.wrong_credentials, Toast.LENGTH_LONG).show()
                        listenerDialog?.onCredentialsDialogFragmentInteraction(Dialog.BUTTON_NEGATIVE)
                    }
                }
            }
        }
    }


    /* Called when the user presses de back button to cancel the dialog */
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
     */
    interface CredentialsDialogFragmentListener {
        fun onCredentialsDialogFragmentInteraction(answer: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        @JvmStatic
        fun newInstance(param1: String, param2: Boolean) =
                CredentialsDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putBoolean(ARG_PARAM2, param2)
                    }
                }
    }
}
