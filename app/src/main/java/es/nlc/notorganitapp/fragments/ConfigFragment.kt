package es.nlc.notorganitapp.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import es.nlc.notorganitapp.R

class ConfigFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "mode") {
            val isDarkMode = sharedPreferences?.getBoolean("mode", false) ?: false
            val nightMode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
            activity?.recreate()
        }
    }
}
