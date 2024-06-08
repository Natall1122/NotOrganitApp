package es.nlc.notorganitapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
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

            colorPreview.setOnClickListener {
                openColorPickerDialogue(colorPreview)
            }

            builder
                .setView(view)
                .setPositiveButton("CREAR") { dialog, id ->
                    val cat = Categories(
                        nom = view.findViewById<EditText>(R.id.new_nom).text.toString(),
                        color = String.format("#%06X", 0xFFFFFF and selectedColor)
                    )
                    mListener.onAddDialogClick(cat)
                }
                .setNegativeButton("CANCEL·LAR") { dialog, id ->
                    dialog.dismiss()
                }
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
