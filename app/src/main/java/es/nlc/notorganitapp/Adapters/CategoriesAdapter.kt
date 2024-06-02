package es.nlc.notorganitapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories

class CategoriesAdapter(private val context: Context?,
                        private val categories: MutableList<Categories>,
                        private val mListener: (Categories) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rec_categories, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val categoria = categories[position]
        holder.bindItem(categoria)
        holder.itemView.setOnClickListener { mListener(categoria) }
    }


    class CategoriesViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val nom: TextView = view.findViewById(R.id.NomCat)
        //private val color: TextView = view.findViewById(R.id.tint)

        fun bindItem(e: Categories){
            nom.text = e.nom
            //color.tint = e.color

        }
    }
}