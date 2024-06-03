package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.CateAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CategoriesFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentCategoriesBinding
    private var mListener: OnButtonsClickedListener? = null
    private lateinit var categoriesAdapter: CateAdapter
    private val categoriesList: MutableList<Categories> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        Log.d("CategoriesFragment", "Layout inflated")
        binding.newCat.setOnClickListener(this)
        Log.d("CategoriesFragment", "Listener set on NewCat button")
        setupRecyclerView()
        fetchCategoriesFromDatabase()
        return binding.root
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

    //  PART EXTRACCIÓ I MOSTRAR DADES

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


    // BOTONS
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is OnButtonsClickedListener){
            mListener = context
        }else{
            throw Exception("The activity must implement the interface OnButtonsFragmentListener")
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.new_cat -> {
                Log.d("CategoriesFragment", "NewCat button clicked")
                Toast.makeText(requireContext(), "Button clicked!", Toast.LENGTH_SHORT).show()
                mListener?.onAddCategory()
            }
        }
    }
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnButtonsClickedListener{
        fun onAddCategory()
    }


}