package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import es.nlc.notorganitapp.R
import es.nlc.notorganitapp.clases.Categories
import es.nlc.notorganitapp.clases.Notes
import es.nlc.notorganitapp.databinding.FragmentNovaNotaBinding
import org.bson.types.ObjectId

class NovaNotaFragment : Fragment(), View.OnClickListener {
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
        binding.MenuNotes.setOnClickListener(this)
        binding.Negreta.setOnClickListener(this)
        binding.Cursiva.setOnClickListener(this)
        binding.subratllat.setOnClickListener(this)

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
            R.id.guardarC -> {
                val nota = Notes(
                    titol = binding.TitolNotaC.text.toString(),
                    text = HtmlCompat.toHtml(binding.textC.text as Spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE),
                    categoria = categoryName,
                    id = ObjectId().toString()
                )
                mListener?.onGuardarNotaCategoria(nota)
            }
            R.id.MenuNotes -> {
                showOptionsPopup(v)
            }
            R.id.Negreta -> {
                applyHtmlStyle("b")
            }
            R.id.Cursiva -> {
                applyHtmlStyle("i")
            }
            R.id.subratllat -> {
                applyHtmlStyle("u")
            }
        }
    }

    private fun showOptionsPopup(anchorView: View) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.opcions_notes, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnButtonsClickedListener {
        fun onCancelarnotaCategoria(Categoria: String)
        fun onGuardarNotaCategoria(nota: Notes)
    }

    private fun applyHtmlStyle(tag: String) {
        val start = binding.textC.selectionStart
        val end = binding.textC.selectionEnd
        val textLength = binding.textC.length()

        if (start < 0 || end <= start || end > textLength) return

        val selectedText = binding.textC.text.substring(start, end)
        val newText = "<$tag>$selectedText</$tag>"

        binding.textC.text.replace(
            start, end,
            HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT),
            0, HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT).length
        )
    }
}
