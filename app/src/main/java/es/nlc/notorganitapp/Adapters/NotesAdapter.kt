package es.nlc.notorganitapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Notes

class NotesAdapter(
    private val context: Context?,
    private val notes: MutableList<Notes>,
    private val mListener: (Notes) -> Unit) : RecyclerView.Adapter<NotesAdapter.EquipViewHolder>() {

    private var isCheckboxVisible = false
    private val selectedNotes = mutableListOf<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rec_notes, parent, false)
        return EquipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun filterNotes(criteria: String) {
        when (criteria) {
            "az" -> notes.sortBy { it.titol }
            "za" -> notes.sortByDescending { it.titol }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EquipViewHolder, position: Int) {
        val note = notes[position]
        holder.bindItem(note, isCheckboxVisible, selectedNotes.contains(note))
        holder.itemView.setOnClickListener { mListener(note) }
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedNotes.add(note)
            } else {
                selectedNotes.remove(note)
            }
        }
    }

    fun CheckBoxVisible() {
        isCheckboxVisible = true
        selectedNotes.clear()
        notifyDataSetChanged()
    }


    fun hideCheckboxes() {
        isCheckboxVisible = false
        selectedNotes.clear()
        notifyDataSetChanged()
    }

    fun getSelectedNotes(): List<Notes> {
        return selectedNotes
    }

    class EquipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titol: TextView = view.findViewById(R.id.titol)
        private val text: TextView = view.findViewById(R.id.text)
        val checkbox: CheckBox = view.findViewById(R.id.borrar)

        fun bindItem(note: Notes, isCheckboxVisible: Boolean, isSelected: Boolean) {
            titol.text = note.titol
            text.text = note.text
            checkbox.visibility = if (isCheckboxVisible) View.VISIBLE else View.INVISIBLE
            checkbox.isChecked = isSelected
        }
    }
}


