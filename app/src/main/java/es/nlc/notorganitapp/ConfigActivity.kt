package es.nlc.notorganitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import es.nlc.notorganitapp.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("mode", false)
        val nightMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        setTheme(R.style.Theme_PreferenceScreen)

        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
