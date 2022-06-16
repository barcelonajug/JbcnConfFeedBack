package cat.cristina.pep.jbcnconffeedback.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.widget.TextView

import cat.cristina.pep.jbcnconffeedback.R

private const val ARG_DESCRIPTION = "description"
private const val ARG_BIOGRAPHY = "biography"
private const val ARG_TWITTER = "twitter"
private const val ARG_HOMEPAGE = "homepage"

/**
 *
 */
class PersonalStuffDialogFragment : DialogFragment() {
    private var description: String? = null
    private var biography: String? = null
    private var twitter: String? = null
    private var homepage: String? = null
    private var listenerPersonalStuffDialog: OnPersonalStuffDialogFragmentListener? = null

    private lateinit var tvDescription: TextView
    private lateinit var tvBiography: TextView
    private lateinit var tvTwitter: TextView
    private lateinit var tvHomepage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            description = it.getString(ARG_DESCRIPTION)
            biography = it.getString(ARG_BIOGRAPHY)
            twitter = it.getString(ARG_TWITTER)
            homepage = it.getString(ARG_HOMEPAGE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.fragment_personal_stuff_dialog, null)

        tvDescription = view.findViewById(R.id.tv_description)
        tvBiography = view.findViewById(R.id.tv_biography)
        tvTwitter = view.findViewById(R.id.tv_twitter)
        tvHomepage = view.findViewById(R.id.tv_homepage)

        tvDescription.text = description
        tvBiography.text = biography
        tvTwitter.text = twitter
        tvHomepage.text = homepage

        val dialog = builder.setView(view)
                .setPositiveButton(R.string.ok, { _, _ ->
                    onButtonPressed("")
                }).create()

        dialog.window.setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame)

        return dialog
    }

    fun onButtonPressed(msg: String) {
        listenerPersonalStuffDialog?.onPersonalStuffDialogFragmentInteraction(msg)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPersonalStuffDialogFragmentListener) {
            listenerPersonalStuffDialog = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnPersonalStuffDialogFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerPersonalStuffDialog = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     */
    interface OnPersonalStuffDialogFragmentListener {
        fun onPersonalStuffDialogFragmentInteraction(msg: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param description Parameter 1.
         * @param biography Parameter 2.
         * @return A new instance of fragment PersonalStuffDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(description: String, biography: String, twitter: String, homepage: String) =
                PersonalStuffDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_DESCRIPTION, description)
                        putString(ARG_BIOGRAPHY, biography)
                        putString(ARG_TWITTER, twitter)
                        putString(ARG_HOMEPAGE, homepage)
                    }
                }
    }
}
