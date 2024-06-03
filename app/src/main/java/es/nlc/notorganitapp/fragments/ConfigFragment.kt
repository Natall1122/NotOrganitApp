package es.nlc.notorganitapp.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import es.nlc.notorganitapp.R

class ConfigFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}