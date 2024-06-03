package es.nlc.notorganitapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.CateAdapter
import es.nlc.notorganitapp.Adapters.CategoriesAdapter
import es.nlc.notorganitapp.Adapters.NotesAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CateAdapter
    private val categoriesList: MutableList<Categories> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchCategoriesFromDatabase()
    }

    private fun setupRecyclerView() {
        categoriesAdapter = CateAdapter(requireContext(), categoriesList) { cate ->
            Toast.makeText(context, "Categoria", Toast.LENGTH_SHORT).show()
        }
        binding.CateRec.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
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

    private suspend fun parseCategoriesResult(result: String) {
        withContext(Dispatchers.Main) {
            try {
                val jsonObject = JSONObject(result)
                val documentsArray: JSONArray = jsonObject.getJSONArray("documents")
                for (i in 0 until documentsArray.length()) {
                    val document = documentsArray.getJSONObject(i)
                    val nom = document.getString("Nom")
                    val color = document.getString("Color")
                    categoriesList.add(Categories(nom, color))
                }
                categoriesAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}