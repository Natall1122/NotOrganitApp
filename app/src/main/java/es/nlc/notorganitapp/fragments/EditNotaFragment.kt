package es.nlc.notorganitapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
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

        val titolNota = arguments?.getString("titol") ?: ""
        val textNota = arguments?.getString("text") ?: ""

        binding.Titolnota.setText(titolNota)
        binding.TextUpdate.setText(HtmlCompat.fromHtml(textNota, HtmlCompat.FROM_HTML_MODE_COMPACT))

        binding.CancelarUpdate.setOnClickListener(this)
        binding.GuardarUpdate.setOnClickListener(this)
        binding.MenuEdit.setOnClickListener(this)
        binding.NegretaU.setOnClickListener(this)
        binding.CursivaU.setOnClickListener(this)
        binding.subratllatU.setOnClickListener(this)
        binding.augmentarU.setOnClickListener(this)
        binding.disminuirU.setOnClickListener(this)

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
                    id = id,
                    titol = binding.Titolnota.text.toString(),
                    text = HtmlCompat.toHtml(binding.TextUpdate.text as Spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE).replace("\"", "\\\"").replace("\n", ""),
                    categoria = categoryName
                )
                mListener?.onGuardarUpdate(nota, id)
            }
            R.id.MenuEdit -> {
                showOptionsPopup(v)
            }
            R.id.NegretaU -> {
                applyHtmlStyle("b")
            }
            R.id.CursivaU -> {
                applyHtmlStyle("i")
            }
            R.id.subratllatU -> {
                applyHtmlStyle("u")
            }
            R.id.augmentarU -> {
                changeFontSizeHtml(1.5f)
            }
            R.id.disminuirU -> {
                changeFontSizeHtml(0.75f)
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
        fun onCancelarUpdate(categoria: String)
        fun onGuardarUpdate(nota: Notes, id: String)
    }

    private fun applyHtmlStyle(tag: String) {
        val start = binding.TextUpdate.selectionStart
        val end = binding.TextUpdate.selectionEnd
        val textLength = binding.TextUpdate.length()

        if (start < 0 || end <= start || end > textLength) return

        val selectedText = binding.TextUpdate.text.substring(start, end)
        val newText = "<$tag>$selectedText</$tag>"

        binding.TextUpdate.text.replace(
            start, end,
            HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT),
            0, HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT).length
        )
    }



    private fun changeFontSizeHtml(scaleFactor: Float) {
        val start = binding.TextUpdate.selectionStart
        val end = binding.TextUpdate.selectionEnd
        val textLength = binding.TextUpdate.length()

        if (start < 0 || end <= start || end > textLength) return

        val selectedText = binding.TextUpdate.text.substring(start, end)
        val newText = "<span style=\"font-size:${scaleFactor}em;\">$selectedText</span>"

        binding.TextUpdate.text.replace(
            start, end,
            HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT),
            0, HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_COMPACT).length
        )
    }
}
