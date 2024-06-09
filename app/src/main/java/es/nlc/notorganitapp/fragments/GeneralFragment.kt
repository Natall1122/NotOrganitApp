package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.nlc.notorganitapp.Adapters.CateAdapter
import es.nlc.notorganitapp.Adapters.NotesAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentGeneralBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GeneralFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentGeneralBinding
    private lateinit var notesAdapter: NotesAdapter
    private val notesList: MutableList<Notes> = mutableListOf()
    private var mListener: OnButtonsClickedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGeneralBinding.inflate(inflater, container, false)
        binding.newNote.setOnClickListener(this)
        binding.borrarNote.setOnClickListener(this)
        binding.cancelarBorrat.setOnClickListener(this)
        binding.ContinuarBorrat.setOnClickListener(this)
        setupRecyclerView()
        fetchNotesFromDatabase()
        return binding.root
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(requireContext(), notesList) { note ->
            mListener?.onEditNoteClicked(note.id, note.titol, note.text, note.categoria)
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
            R.id.new_note -> {
                mListener?.onAddNote()
            }
            R.id.borrar_note -> {
                notesAdapter.CheckBoxVisible()
                binding.cancelarBorrat.visibility = View.VISIBLE
                binding.ContinuarBorrat.visibility = View.VISIBLE
            }
            R.id.cancelarBorrat -> {
                notesAdapter.hideCheckboxes()
                binding.cancelarBorrat.visibility = View.INVISIBLE
                binding.ContinuarBorrat.visibility = View.INVISIBLE
            }
            R.id.ContinuarBorrat -> {
                deleteSelectedNotes()
            }
        }
    }

    private fun deleteSelectedNotes() {
        val selectedNotes = notesAdapter.getSelectedNotes()
        val ids = selectedNotes.map { it.id }

        CoroutineScope(Dispatchers.IO).launch {
            val filter = JSONObject().put("_id", JSONObject().put("\$in", JSONArray(ids))).toString()
            val result = MongoDBDataAPIClient.deleteMany("Notes", "Categories", "Cluster0", filter)
            withContext(Dispatchers.Main) {
                if (result != null) {
                    notesAdapter.notifyDataSetChanged()
                    notesList.removeAll(selectedNotes)
                    notesAdapter.hideCheckboxes()
                    binding.cancelarBorrat.visibility = View.INVISIBLE
                    binding.ContinuarBorrat.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(context, "Error deleting notes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnButtonsClickedListener {
        fun onEditNoteClicked(id: String, titol: String, text: String, categoria: String)
        fun onAddNote()
    }
}

