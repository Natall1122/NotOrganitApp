package es.nlc.notorganitapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories

class UpdateCategoriaDialog(private val Info: Categories): DialogFragment() {
    private lateinit var mListener: DialogListener

    interface DialogListener{
        fun onUpdateDialogClick(cat: Categories, NomAntic: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is DialogListener){
            mListener = context
        }else{
            throw Exception("must implement interface")
        }
    }

    override fun onCreateDialog(savedInstantState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_update, null)

            val titol = view.findViewById<TextView>(R.id.titolCat)
            val nom = view.findViewById<EditText>(R.id.canvi_nom)
            val color = view.findViewById<EditText>(R.id.canvi_color)

            titol.setText(Info.nom)
            nom.setText(Info.nom)
            color.setText(Info.color)

            val nomAntic = Info.nom

            builder
                .setView(view)
                .setPositiveButton("MODIFICAR"){ dialog, id ->
                    val cat = Categories(
                        nom = nom.text.toString(),
                        color = color.text.toString()
                    )
                    mListener.onUpdateDialogClick(cat, nomAntic)
                }
                .setNegativeButton("CANCEL·LAR"){dialog, id ->

                }
            builder.create()
        }?: throw IllegalStateException("Activitat No pot ser nula")
    }
}