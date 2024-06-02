package es.nlc.notorganitapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Notes

class NotesAdapter(private val context: Context?,
                   private val notes: MutableList<Notes>,
                   private val mListener: (Notes) -> Unit) :
    RecyclerView.Adapter<NotesAdapter.EquipViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rec_notes, parent, false)
        return EquipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: EquipViewHolder, position: Int) {
        val jugador = notes[position]
        holder.bindItem(jugador)
        holder.itemView.setOnClickListener { mListener(jugador) }
    }


    class EquipViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val titol: TextView = view.findViewById(R.id.titol)
        private val text: TextView = view.findViewById(R.id.text)

        fun bindItem(e: Notes){
            titol.text = e.titol
            text.text = e.text

        }
    }
}