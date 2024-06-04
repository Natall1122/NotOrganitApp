package es.nlc.notorganitapp

import android.annotation.SuppressLint
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
import es.nlc.notorganitapp.fragments.CategoriesFragment
import es.nlc.notorganitapp.fragments.GeneralFragment
import es.nlc.notorganitapp.fragments.PrincipalFragment
import es.nlc.notorganitapp.databinding.ActivityMainBinding
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.dialogs.NovaCategoriaDialog
import es.nlc.notorganitapp.fragments.CategoriaConcretaFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NovaCategoriaDialog.DialogListener, NavigationView.OnNavigationItemSelectedListener, PrincipalFragment.OnPrincipalClickedListener ,CategoriesFragment.OnButtonsClickedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NotOrganitApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        setUpNavigationDrawer()
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

    override fun onAddCategory() {
        NovaCategoriaDialog().show(supportFragmentManager,"")
    }

    override fun onAddDialogClick(cat: Categories){
        Toast.makeText(this, "HOLAAA", Toast.LENGTH_SHORT).show()
        val document = """
            {
                "Nom": ${cat.nom}
                "Color": ${cat.color}
            }
        """

        lifecycleScope.launch(Dispatchers.IO){
            try {
                val result = MongoDBDataAPIClient.insertOne("Categoria", "Categories", "Cluster0", document)
                println(result)
            }catch (e: Exception){
            }
        }
    }

    override fun onCategories() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CategoriesFragment>(R.id.fragment_container)
            addToBackStack(null)
        }
    }

    override fun onNotes() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<GeneralFragment>(R.id.fragment_container)
            addToBackStack(null)
        }
    }

    override fun onCategoriaClicked(categoryName: String) {
        val fragment = CategoriaConcretaFragment().apply {
            arguments = Bundle().apply {
                putString("CATEGORY_NAME", categoryName)
            }
        }
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }


}