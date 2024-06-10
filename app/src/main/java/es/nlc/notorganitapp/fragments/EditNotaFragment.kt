package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentEditNotaBinding
import org.bson.types.ObjectId

class EditNotaFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditNotaBinding
    private var mListener: OnButtonsClickedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditNotaBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        // Get the note details from the arguments
        val titolNota = arguments?.getString("titol") ?: ""
        val textNota = arguments?.getString("text") ?: ""

        // Set the text directly
        binding.Titolnota.setText(titolNota)
        binding.TextUpdate.setText(textNota)

        // Set click listeners
        binding.CancelarUpdate.setOnClickListener(this)
        binding.GuardarUpdate.setOnClickListener(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
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
        val categoryName = arguments?.getString("categoria") ?: ""
        val idNota = arguments?.getString("id") ?: ""
        val id = idNota
        when (v.id) {
            R.id.CancelarUpdate -> {
                mListener?.onCancelarUpdate(categoryName)
            }
            R.id.GuardarUpdate -> {
                val nota = Notes(
                    id = id,  // Use the existing id
                    titol = binding.Titolnota.text.toString(),
                    text = binding.TextUpdate.text.toString(),
                    categoria = categoryName
                )
                mListener?.onGuardarUpdate(nota, id)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnButtonsClickedListener {
        fun onCancelarUpdate(categoria: String)
        fun onGuardarUpdate(nota: Notes, id: String)
    }
}