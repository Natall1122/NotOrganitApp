package es.nlc.notorganitapp.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.NotesAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentPrincipalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PrincipalFragment : Fragment() {
    private lateinit var binding: FragmentPrincipalBinding
    private lateinit var notesAdapter: NotesAdapter
    private val notesList: MutableList<Notes> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchNotesFromDatabase()
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(requireContext(), notesList) { note ->
            Toast.makeText(context, "Nota", Toast.LENGTH_SHORT).show()
        }
        binding.notesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = notesAdapter
        }
    }

    private fun fetchNotesFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = MongoDBDataAPIClient.findMany("Receptes", "Categories", "Cluster0")
            println("Result from findMany: $result")
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
                println("HOLAAAAAAAAAAA $documentsArray")
                for (i in 0 until documentsArray.length()) {
                    val document = documentsArray.getJSONObject(i)
                    val id = document.getString("_id")
                    val title = document.getString("titol")
                    val text = document.getString("text")
                    notesList.add(Notes(id, title, text))
                }
                println("Total notes: ${notesList.size}")
                notesAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
