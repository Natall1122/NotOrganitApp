package es.nlc.notorganitapp.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories

class CateAdapter(
    private val context: Context?,
    private val categories: MutableList<Categories>,
    private val mListener: (Categories) -> Unit,
    private val onMenuClick: (Categories, View) -> Unit
) : RecyclerView.Adapter<CateAdapter.CategoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rec_cate, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val categoria = categories[position]
        holder.bindItem(categoria)
        holder.itemView.setOnClickListener { mListener(categoria) }
        holder.menuCat.setOnClickListener { onMenuClick(categoria, holder.menuCat) }
    }

    class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nom: TextView = view.findViewById(R.id.NomCate)
        val menuCat: ImageView = view.findViewById(R.id.menuCat)

        fun bindItem(e: Categories) {
            nom.text = e.nom
            try {
                // Converteix la cadena de color en un color
                val color = Color.parseColor(e.color)
                // Estableix el color de fons de la vista de la categoria
                nom.setBackgroundColor(color)
            } catch (ex: IllegalArgumentException) {
                // Maneja la situació en cas que el color no sigui vàlid
                nom.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}
