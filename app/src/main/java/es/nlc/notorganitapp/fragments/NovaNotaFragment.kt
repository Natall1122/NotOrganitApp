package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentNovaNotaBinding
import org.bson.types.ObjectId

class NovaNotaFragment: Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentNovaNotaBinding
    private var mListener: OnButtonsClickedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNovaNotaBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()


        binding.cancelarC.setOnClickListener(this)
        binding.guardarC.setOnClickListener(this)


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
        val categoryName = arguments?.getString("CATEGORY_NAME") ?: ""
        when (v.id) {
            R.id.cancelarC -> {
                mListener?.onCancelarnotaCategoria(categoryName)
            }
            R.id.guardarC ->{
                val nota = Notes(
                    titol = binding.TitolNotaC.text.toString(),
                    text = binding.textC.text.toString(),
                    categoria = categoryName,
                    id = ObjectId().toString()
                )
                mListener?.onGuardarNotaCategoria(nota)
            }
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnButtonsClickedListener {
        fun onCancelarnotaCategoria(Categoria: String)
        fun onGuardarNotaCategoria(nota: Notes)
    }
}
