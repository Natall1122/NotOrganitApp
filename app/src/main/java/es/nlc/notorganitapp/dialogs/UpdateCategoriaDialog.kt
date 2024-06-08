package es.nlc.notorganitapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import yuku.ambilwarna.AmbilWarnaDialog

class UpdateCategoriaDialog(private val Info: Categories) : DialogFragment() {
    private lateinit var mListener: DialogListener
    private var selectedColor: Int = 0

    interface DialogListener {
        fun onUpdateDialogClick(cat: Categories, NomAntic: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DialogListener) {
            mListener = context
        } else {
            throw Exception("must implement interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_update, null)

            val titol = view.findViewById<TextView>(R.id.titolCat)
            val nom = view.findViewById<EditText>(R.id.canvi_nom)
            val colorView = view.findViewById<View>(R.id.canvi_color)
            
            selectedColor = Color.parseColor(Info.color)

            titol.text = Info.nom
            nom.setText(Info.nom)
            colorView.setBackgroundColor(selectedColor)

            colorView.setOnClickListener {
                openColorPickerDialogue(colorView)
            }

            val nomAntic = Info.nom

            builder
                .setView(view)
                .setPositiveButton("MODIFICAR") { dialog, id ->
                    val cat = Categories(
                        nom = nom.text.toString(),
                        color = String.format("#%06X", 0xFFFFFF and selectedColor)
                    )
                    mListener.onUpdateDialogClick(cat, nomAntic)
                }
                .setNegativeButton("CANCEL·LAR") { dialog, id ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activitat No pot ser nula")
    }

    private fun openColorPickerDialogue(colorView: View) {
        val colorPickerDialogue = AmbilWarnaDialog(requireContext(), selectedColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    selectedColor = color
                    colorView.setBackgroundColor(selectedColor)
                }
            })
        colorPickerDialogue.show()
    }
}
