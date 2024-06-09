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
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.dialogs.NovaCategoriaDialog
import es.nlc.notorganitapp.dialogs.UpdateCategoriaDialog
import es.nlc.notorganitapp.fragments.CategoriaConcretaFragment
import es.nlc.notorganitapp.fragments.EditNotaFragment
import es.nlc.notorganitapp.fragments.NovaNotaFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.json.JSONObject


class MainActivity : AppCompatActivity(),EditNotaFragment.OnButtonsClickedListener, CategoriaConcretaFragment.OnButtonsClickedListener, NovaNotaFragment.OnButtonsClickedListener, GeneralFragment.OnButtonsClickedListener, UpdateCategoriaDialog.DialogListener, NovaCategoriaDialog.DialogListener, NavigationView.OnNavigationItemSelectedListener, PrincipalFragment.OnPrincipalClickedListener ,CategoriesFragment.OnButtonsClickedListener {
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

    // CREAR CATEGORIA DIALOG
    override fun onAddCategory() {
        NovaCategoriaDialog().show(supportFragmentManager,"")
    }

    override fun onAddDialogClick(cat: Categories){
        val document = JSONObject().apply {
            put("Nom", cat.nom)
            put("Color", cat.color)
        }.toString()

        lifecycleScope.launch(Dispatchers.IO){
            try {
                MongoDBDataAPIClient.insertOne("Categoria", "Categories", "Cluster0", document)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CategoriesFragment>(R.id.fragment_container)
                    addToBackStack(null)
                }

            }catch (e: Exception){
                println("ERROR: ${e}")
            }
        }
    }

    // OBRIR CATEGORIES I NOTES GENERALS
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

    // OBRIR CATEGORIA CONCRETA
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

    // BORRAR I EDITAR CATEGORIA
    override fun onDeleteCategory(categoryName: String) {
        lifecycleScope.launch(Dispatchers.IO){
            try {
                MongoDBDataAPIClient.deleteNotesByCategory("Categoria", "Categories", "Cluster0", categoryName)
                MongoDBDataAPIClient.deleteCategory("Categoria", "Categories", "Cluster0", categoryName)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CategoriesFragment>(R.id.fragment_container)
                    addToBackStack(null)
                }
            }catch (e: Exception){
                println("ERROR: ${e}")
            }
        }

    }

    override fun onEditCategory(cate: Categories) {
        UpdateCategoriaDialog(cate).show(supportFragmentManager,"")
    }

    override fun onUpdateDialogClick(cat: Categories, nomAntic: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val filter = """{"Nom": "${nomAntic}"}"""
            val update = """{"${'$'}set": {"Nom": "${cat.nom}", "Color": "${cat.color}"}}"""

            val filterNotes = """{"categoria": "${nomAntic}"}"""
            val updateNotes = """{"${'$'}set": {"categoria": "${cat.nom}"}}"""

            MongoDBDataAPIClient.updateOne("Categoria", "Categories", "Cluster0", filter, update)
            MongoDBDataAPIClient.updateNotesCategoria("Notes", "Categories", "Cluster0", filterNotes, updateNotes)

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<CategoriesFragment>(R.id.fragment_container)
                addToBackStack(null)
            }
        }
    }

    // AFEGIR NOTES
    override fun onAddNote() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<NovaNotaFragment>(R.id.fragment_container)
            addToBackStack(null)
        }
    }



    override fun onAddNoteCategoria(categoryName: String) {
        val fragment = NovaNotaFragment().apply {
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

    override fun onCancelarnotaCategoria(categoryName: String) {
        if(categoryName == ""){
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<GeneralFragment>(R.id.fragment_container)
                addToBackStack(null)
            }
        }else {
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

    override fun onGuardarNotaCategoria(nota: Notes) {

        val document = JSONObject().apply {
            put("_id", nota.id)
            put("titol", nota.titol)
            put("text", nota.text)
            put("categoria", nota.categoria)
        }.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                MongoDBDataAPIClient.insertOne("Notes", "Categories", "Cluster0", document)

                if(nota.categoria == ""){
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<GeneralFragment>(R.id.fragment_container)
                        addToBackStack(null)
                    }
                }else {
                    val fragment = CategoriaConcretaFragment().apply {
                        arguments = Bundle().apply {
                            putString("CATEGORY_NAME", nota.categoria)
                        }
                    }
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace(R.id.fragment_container, fragment)
                        addToBackStack(null)
                    }
                }
            } catch (e: Exception) {
                println("ERROR: ${e}")
            }
        }
    }

    // EDITAR NOTA
    override fun onEditNoteClicked(id: String, titol: String, text: String, categoria: String) {
        Toast.makeText(this, titol, Toast.LENGTH_SHORT).show()
        val fragment = EditNotaFragment().apply {
            arguments = Bundle().apply {
                putString("id", id)
                putString("titol", titol)
                putString("text", text)
                putString("categoria", categoria)
            }
        }
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }

    }


    override fun onCancelarUpdate(Categoria: String) {
        if(Categoria == ""){
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<GeneralFragment>(R.id.fragment_container)
                addToBackStack(null)
            }
        }else {
            val fragment = CategoriaConcretaFragment().apply {
                arguments = Bundle().apply {
                    putString("CATEGORY_NAME", Categoria)
                }
            }
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container, fragment)
                addToBackStack(null)
            }
        }
    }

    override fun onGuardarUpdate(nota: Notes, id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val filter = """{"_id": "${id}"}"""
            val update = """{"${'$'}set": {"titol": "${nota.titol}", "text": "${nota.text}"}}"""

            MongoDBDataAPIClient.updateOne("Notes", "Categories", "Cluster0", filter, update)

            if(nota.categoria == ""){
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<GeneralFragment>(R.id.fragment_container)
                    addToBackStack(null)
                }
            }else {
                val fragment = CategoriaConcretaFragment().apply {
                    arguments = Bundle().apply {
                        putString("CATEGORY_NAME", nota.categoria)
                    }
                }
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.fragment_container, fragment)
                    addToBackStack(null)
                }
            }
        }
    }

}