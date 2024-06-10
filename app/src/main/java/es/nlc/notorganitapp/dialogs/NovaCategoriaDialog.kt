package es.nlc.notorganitapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import yuku.ambilwarna.AmbilWarnaDialog

class NovaCategoriaDialog : DialogFragment() {
    private lateinit var mListener: DialogListener
    private var selectedColor: Int = 0

    interface DialogListener {
        fun onAddDialogClick(cat: Categories)
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
            val view = inflater.inflate(R.layout.dialog_add, null)

            val colorPreview = view.findViewById<View>(R.id.new_color)
            val guardar = view.findViewById<Button>(R.id.GuardarCategoria)
            val cancelar = view.findViewById<ImageView>(R.id.CancelarNovaC)
            val editText = view.findViewById<EditText>(R.id.new_nom)

            guardar.setOnClickListener {
                val cat = Categories(
                    nom = editText.text.toString(),
                    color = String.format("#%06X", 0xFFFFFF and selectedColor)
                )
                mListener.onAddDialogClick(cat)
                dismiss()
            }

            cancelar.setOnClickListener{
               dismiss()
            }

            colorPreview.setOnClickListener {
                openColorPickerDialogue(colorPreview)
            }

            builder.setView(view)
            builder.create()

        } ?: throw IllegalStateException("Activitat No pot ser nula")
    }

    private fun openColorPickerDialogue(colorPreview: View) {
        val colorPickerDialogue = AmbilWarnaDialog(requireContext(), selectedColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    selectedColor = color
                    colorPreview.setBackgroundColor(selectedColor)
                }
            })
        colorPickerDialogue.show()
    }
}
