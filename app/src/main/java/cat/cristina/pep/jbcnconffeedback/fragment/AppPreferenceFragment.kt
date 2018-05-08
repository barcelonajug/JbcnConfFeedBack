package cat.cristina.pep.jbcnconffeedback.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import cat.cristina.pep.jbcnconffeedback.R
import cat.cristina.pep.jbcnconffeedback.utils.PreferenceKeys

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 */
class AppPreferenceFragment :
        PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = AppPreferenceFragment::class.java.name

    lateinit var sharedPreferences: SharedPreferences

    /**
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        var summary = if (sharedPreferences.getBoolean(PreferenceKeys.VIBRATOR_KEY, true))
            "Enabled" else "Disabled"
        var preference = findPreference(PreferenceKeys.VIBRATOR_KEY)
        preference.summary = summary

        summary = sharedPreferences.getString(PreferenceKeys.ROOM_KEY, "Undefined")
        preference = findPreference(PreferenceKeys.ROOM_KEY)
        preference.summary = summary

        // vbc, hbc, lbc
        summary = sharedPreferences.getString(PreferenceKeys.CHART_TYPE_KEY, "vbc")
        preference = findPreference(PreferenceKeys.CHART_TYPE_KEY)
        preference.summary = summary
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
                val summary = if (sharedPreferences!!.getBoolean(key, true)) "Enabled" else "Disabled"
                preference.summary = summary
                sharedPreferences.edit().putBoolean(key, sharedPreferences.getBoolean(key, true))
            }
            PreferenceKeys.ROOM_KEY -> {
                val summary = sharedPreferences!!.getString(key, "Undefined")
                preference.summary = summary
                sharedPreferences.edit().putString(key, sharedPreferences.getString(key, "Undefined"))
            }
            PreferenceKeys.CHART_TYPE_KEY -> {
                val summary = sharedPreferences!!.getString(key, "Undefined")
                preference.summary = summary
                sharedPreferences.edit().putString(key, sharedPreferences.getString(key, "Undefined"))
            }
        }
    }
}
