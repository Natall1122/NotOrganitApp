package es.nlc.notorganitapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.CateAdapter
import es.nlc.notorganitapp.Adapters.NotesAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentGeneralBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GeneralFragment : Fragment() {

    private lateinit var binding: FragmentGeneralBinding
    private lateinit var notesAdapter: NotesAdapter
    private val notesList: MutableList<Notes> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGeneralBinding.inflate(inflater, container, false)
        //binding.newNot.setOnClickListener(this)
        setupRecyclerView()
        fetchNotesFromDatabase()
        return binding.root
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(requireContext(), notesList) { cate ->
            Toast.makeText(context, "Categoria", Toast.LENGTH_SHORT).show()
        }
        binding.NoteRec.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = notesAdapter
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

    private suspend fun parseNotesResult(result: String) {
        withContext(Dispatchers.Main) {
            try {
                val jsonObject = JSONObject(result)
                val documentsArray: JSONArray = jsonObject.getJSONArray("documents")
                for (i in 0 until documentsArray.length()) {
                    val document = documentsArray.getJSONObject(i)
                    val id =document.getString("_id")
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
}