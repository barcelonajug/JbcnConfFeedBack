package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.utils.PreferenceKeys

/**
 */
class AppPreferenceFragment :
        PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = AppPreferenceFragment::class.java.name

    private lateinit var sharedPreferences: SharedPreferences
    private var listener: OnAppPreferenceFragmentListener? = null
    private lateinit var previousRoomName: String
    private var previousAutoMode: Boolean = false

    /**
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        var summary = if (sharedPreferences.getBoolean(PreferenceKeys.VIBRATOR_KEY, false))
            "On" else "Off"
        var preference = findPreference(PreferenceKeys.VIBRATOR_KEY)
        preference.summary = summary

        summary = sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name))
        preference = findPreference(PreferenceKeys.ROOM_KEY)
        preference.summary = summary
        previousRoomName = summary

        previousAutoMode = sharedPreferences.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false)
        summary = if (previousAutoMode) "On" else "Off"
        preference = findPreference(PreferenceKeys.AUTO_MODE_KEY)
        preference.summary = summary

        // Vertical/Horizontal/Lineal Bar Chart
//        summary = sharedPreferences.getString(PreferenceKeys.CHART_TYPE_KEY, "Vertical Bar Chart")
//        preference = findPreference(PreferenceKeys.CHART_TYPE_KEY)
//        preference.summary = summary
    }

    override fun onResume() {
        super.onResume()
        //unregister the preferenceChange listener
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        //unregister the preference change listener
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AppPreferenceFragment.OnAppPreferenceFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnAppPreferenceFragmentListener")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (sharedPreferences.getString(PreferenceKeys.ROOM_KEY, resources.getString(R.string.pref_default_room_name)) != previousRoomName
                || sharedPreferences.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false) != previousAutoMode) {
            listener?.onAppPreferenceFragment(sharedPreferences.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false))
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnAppPreferenceFragmentListener {
        fun onAppPreferenceFragment(autoMode: Boolean)
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     *
     * This callback will be run on your main thread.
     *
     * @param sharedPreferences The [SharedPreferences] that received
     * the change.
     * @param key The key of the preference that was changed, added, or
     * removed.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        val preference = findPreference(key)

        when (key) {
            PreferenceKeys.VIBRATOR_KEY -> {
                val vibrator = sharedPreferences!!.getBoolean(key, false)
                val summary = if (vibrator) "On" else "Off"
                preference.summary = summary
                sharedPreferences.edit().putBoolean(key, vibrator).commit()
            }
            PreferenceKeys.ROOM_KEY -> {
                val summary = sharedPreferences!!.getString(key, resources.getString(R.string.pref_default_room_name))
                val mode = sharedPreferences!!.getBoolean(PreferenceKeys.AUTO_MODE_KEY, false)
                preference.summary = summary
                sharedPreferences.edit().putString(key, summary).commit()
                // al listener se l'ha de cridar des d'onDestroy
                //listener?.onAppPreferenceFragment(mode)
            }
            PreferenceKeys.AUTO_MODE_KEY -> {
                val mode = sharedPreferences!!.getBoolean(key, false)
                val summary = if (mode) "On" else "Off"
                preference.summary = summary
                sharedPreferences.edit().putBoolean(key, mode).commit()
                // al listener se l'ha de cridar des d'onDestroy
                //listener?.onAppPreferenceFragment(mode)
            }

            PreferenceKeys.EMAIL -> {
                val summary = sharedPreferences!!.getString(key, resources.getString(R.string.pref_default_email))
                preference.summary = summary
                sharedPreferences.edit().putString(key, summary).commit()
            }

//            PreferenceKeys.CHART_TYPE_KEY -> {
//                val summary = sharedPreferences!!.getString(key, resources.getString(R.string.pref_default_chart_type))
//                preference.summary = summary
//                sharedPreferences.edit().putString(key, summary).commit()
//            }
        }
    }
}
