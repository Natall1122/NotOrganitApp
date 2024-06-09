package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.CategoriesAdapter
import es.nlc.notorganitapp.Adapters.NotesAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentPrincipalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PrincipalFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentPrincipalBinding
    private lateinit var notesAdapter: NotesAdapter
    private var mListener2: OnPrincipalClickedListener? = null
    private val notesList: MutableList<Notes> = mutableListOf()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val categoriesList: MutableList<Categories> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        binding.linearCategories.setOnClickListener(this)
        binding.linearNotes.setOnClickListener(this)


        setupRecyclerView()
        fetchNotesFromDatabase()
        fetchCategoriesFromDatabase()
        return binding.root
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(requireContext(), notesList) { note ->
            mListener2?.onEditNoteClicked(note.id, note.titol, note.text, note.categoria)
        }
        binding.notesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = notesAdapter
        }

        categoriesAdapter = CategoriesAdapter(requireContext(), categoriesList) { cate ->
            mListener2?.onCategoriaClicked(cate.nom)
        }
        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun fetchNotesFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = MongoDBDataAPIClient.findMany("Notes", "Categories", "Cluster0")
            result?.let {
                parseNotesResult(it)
            }
        }
    }

    private fun fetchCategoriesFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = MongoDBDataAPIClient.findMany("Categoria", "Categories", "Cluster0")
            result?.let {
                parseCategoriesResult(it)
            }
        }
    }

    private suspend fun parseNotesResult(result: String) {
        withContext(Dispatchers.Main) {
            try {
                val jsonObject = JSONObject(result)
                val documentsArray: JSONArray = jsonObject.getJSONArray("documents")
                for (i in 0 until documentsArray.length()) {
                    val document = documentsArray.getJSONObject(i)
                    val id = document.getString("_id")
                        val title = document.getString("titol")
                        val text = document.getString("text")
                        val categoria = document.getString("categoria")
                    if (!notesList.any { it.id == id }) {
                        notesList.add(Notes(id, title, text, categoria))
                    }
                }
                notesAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun parseCategoriesResult(result: String) {
        withContext(Dispatchers.Main) {
            try {
                val jsonObject = JSONObject(result)
                val documentsArray: JSONArray = jsonObject.getJSONArray("documents")
                for (i in 0 until documentsArray.length()) {
                    val document = documentsArray.getJSONObject(i)
                    val nom = document.getString("Nom")
                    val color = document.getString("Color")
                    if (!categoriesList.any { it.nom == nom }) {
                        categoriesList.add(Categories(nom, color))
                    }
                }
                categoriesAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnPrincipalClickedListener) {
            mListener2 = context
        } else {
            throw Exception("The activity must implement the interface OnButtonsFragmentListener")
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.linearCategories -> {
                mListener2?.onCategories()
            }
            R.id.linearNotes -> {
                mListener2?.onNotes()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener2 = null
    }

    interface OnPrincipalClickedListener {
        fun onEditNoteClicked(id: String, titol: String, text: String, categoria: String)
        fun onCategories()
        fun onNotes()
        fun onCategoriaClicked(categoryName: String)
    }
}
