package es.nlc.notorganitapp.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.CateAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.databinding.FragmentCategoriesBinding
import es.nlc.notorganitapp.dialogs.UpdateCategoriaDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CategoriesFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentCategoriesBinding
    private var mListener: OnButtonsClickedListener? = null
    lateinit var categoriesAdapter: CateAdapter
    val categoriesList: MutableList<Categories> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        binding.newCat.setOnClickListener(this)
        setupRecyclerView()
        fetchCategoriesFromDatabase()
        return binding.root
    }

    private fun setupRecyclerView() {
        categoriesAdapter = CateAdapter(requireContext(), categoriesList,
            { cate -> mListener?.onCategoriaClicked(cate.nom) },
            { cate, view -> showOptionsPopup(cate, view) }
        )
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

    private fun showOptionsPopup(cate: Categories, anchorView: View) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.opcions_carpetes, null)

        val deleteIcon = popupView.findViewById<ImageView>(R.id.delete_icon)
        val editIcon = popupView.findViewById<ImageView>(R.id.edit_icon)

        val color = Color.parseColor(cate.color)
        popupView.setBackgroundColor(color)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        deleteIcon.setOnClickListener {
            mListener?.onDeleteCategory(cate.nom)
            popupWindow.dismiss()
        }

        editIcon.setOnClickListener {
            mListener?.onEditCategory(cate)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnButtonsClickedListener) {
            mListener = context
        } else {
            throw Exception("The activity must implement the interface OnButtonsFragmentListener")
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.new_cat -> {
                mListener?.onAddCategory()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnButtonsClickedListener {
        fun onAddCategory()
        fun onCategoriaClicked(categoryName: String)
        fun onDeleteCategory(categoryName: String)
        fun onEditCategory(cate: Categories)
    }
}
