package es.nlc.notorganitapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories

class NovaCategoriaDialog: DialogFragment() {
    private lateinit var mListener: DialogListener

    interface DialogListener{
        fun onAddDialogClick(cat: Categories)
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
            val view = inflater.inflate(R.layout.dialog_add, null)
            builder
                .setView(view)
                .setPositiveButton("CREAR"){ dialog, id ->
                    val cat = Categories(
                        nom = view.findViewById<EditText>(R.id.new_nom).text.toString(),
                        color = view.findViewById<EditText>(R.id.new_color).text.toString()
                    )
                    mListener.onAddDialogClick(cat)
                }
                .setNegativeButton("CANCEL"){dialog, id ->

                }
            builder.create()
        }?: throw IllegalStateException("Activitat No pot ser nula")
    }
}