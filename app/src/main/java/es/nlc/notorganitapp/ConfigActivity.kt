package es.nlc.notorganitapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.nlc.notorganitapp.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_PreferenceScreen)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}