package es.nlc.notorganitapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import es.nlc.notorganitapp.Fragments.CategoriesFragment
import es.nlc.notorganitapp.Fragments.GeneralFragment
import es.nlc.notorganitapp.Fragments.PrincipalFragment
import es.nlc.notorganitapp.Mongo.DatabaseOperations
import es.nlc.notorganitapp.databinding.ActivityMainBinding
import es.nlc.notorganitapp.Mongo.MongoDBClient
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import kotlinx.coroutines.launch
import org.bson.Document
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    // private lateinit var databaseOperations: DatabaseOperations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NotOrganitApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setUpNavigationDrawer()

        lifecycleScope.launch {
           /* val newDocument = JSONObject()
            newDocument.put("titol", "UEEEEEEEEEEE")
            newDocument.put("text", "FUNCIONA")
            val insertOneResult = MongoDBDataAPIClient.insertOne("Altres", "Categories", "Cluster0", newDocument.toString())
            println("Insert One Result: $insertOneResult")
*/
            val result = MongoDBDataAPIClient.findOne("Receptes", "Categories", "Cluster0")
            if (result != null) {
                println("Data: $result")
                Toast.makeText(this@MainActivity, "Holaaa", Toast.LENGTH_SHORT).show()
            } else {
                println("Failed to fetch data")
            }
        }



    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setUpNavigationDrawer(){
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.myToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this);
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //Close the navigation drawer
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return when(item.itemId){
            R.id.nav_categories -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CategoriesFragment>(R.id.fragment_container)
                    addToBackStack(null)
                }
                true
            }
            R.id.nav_general -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<GeneralFragment>(R.id.fragment_container)
                    addToBackStack(null)
                }
                true
            }
            R.id.nav_principal -> {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<PrincipalFragment>(R.id.fragment_container)
                    addToBackStack(null)
                }
                true
            }
            R.id.nav_config -> {
                startActivity(
                    Intent(this, ConfigActivity::class.java).apply {  }
                )
                true
            }
            else -> false
        }
    }


}