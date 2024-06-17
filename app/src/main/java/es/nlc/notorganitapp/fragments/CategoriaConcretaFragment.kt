package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import es.nlc.notorganitapp.Adapters.NotesAdapter
import es.nlc.notorganitapp.Mongo.MongoDBDataAPIClient
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentCategoriaConcretaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CategoriaConcretaFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentCategoriaConcretaBinding
    private lateinit var notesAdapter: NotesAdapter
    private val notesList: MutableList<Notes> = mutableListOf()
    private var mListener: OnButtonsClickedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriaConcretaBinding.inflate(inflater, container, false)
        binding.newNoteC.setOnClickListener(this)
        binding.borrarNoteC.setOnClickListener(this)
        binding.cancelarBorratC.setOnClickListener(this)
        binding.ContinuarBorratC.setOnClickListener(this)
        binding.filtrarNoteC.setOnClickListener(this)
        binding.buscarC.isEnabled = false
        val categoryName = arguments?.getString("CATEGORY_NAME") ?: ""
        binding.Titol.text = categoryName.uppercase()

        setupRecyclerView()
        fetchNotesFromDatabase(categoryName)
        return binding.root
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(requireContext(), notesList) { note ->
            mListener?.onEditNoteClicked(note.id, note.titol, note.text, note.categoria)
        }
        binding.NotesConcretes.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = notesAdapter
        }
    }

    private fun fetchNotesFromDatabase(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = MongoDBDataAPIClient.findNotesByCategory(category, "Notes", "Categories", "Cluster0")
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
                    val titol = document.getString("titol")
                    val text = document.getString("text")
                    val categoria = document.getString("categoria")
                    if (!notesList.any { it.id == id }) {
                        notesList.add(Notes(id, titol, text, categoria))
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
            R.id.new_note_C -> {
                val categoryName = arguments?.getString("CATEGORY_NAME") ?: ""
                mListener?.onAddNoteCategoria(categoryName)
            }
            R.id.borrar_note_C -> {
                notesAdapter.CheckBoxVisible()
                binding.cancelarBorratC.visibility = View.VISIBLE
                binding.ContinuarBorratC.visibility = View.VISIBLE
            }
            R.id.cancelarBorratC -> {
                notesAdapter.hideCheckboxes()
                binding.cancelarBorratC.visibility = View.INVISIBLE
                binding.ContinuarBorratC.visibility = View.INVISIBLE
            }
            R.id.ContinuarBorratC -> {
                deleteSelectedNotes()
            }
            R.id.filtrar_note_C -> {
                showOptionsPopup(v)
            }
        }
    }

    private fun showOptionsPopup(anchorView: View) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.filtres, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        popupView.findViewById<View>(R.id.az).setOnClickListener {
            notesAdapter.filterNotes("az")
            popupWindow.dismiss()
        }
        popupView.findViewById<View>(R.id.za).setOnClickListener {
            notesAdapter.filterNotes("za")
            popupWindow.dismiss()
        }
        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    private fun deleteSelectedNotes() {
        val selectedNotes = notesAdapter.getSelectedNotes()
        val ids = selectedNotes.map { it.id }

        CoroutineScope(Dispatchers.IO).launch {
            val filter = JSONObject().put("_id", JSONObject().put("\$in", JSONArray(ids))).toString()
            val result = MongoDBDataAPIClient.deleteMany("Notes", "Categories", "Cluster0", filter)
            withContext(Dispatchers.Main) {
                if (result != null) {
                    notesList.removeAll(selectedNotes)
                    notesAdapter.hideCheckboxes()
                    binding.cancelarBorratC.visibility = View.INVISIBLE
                    binding.ContinuarBorratC.visibility = View.INVISIBLE
                    notesAdapter.notifyDataSetChanged()
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
        fun onAddNoteCategoria(NomCategoria: String)
    }
}
