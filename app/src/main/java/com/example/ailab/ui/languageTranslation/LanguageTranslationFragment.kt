package com.example.ailab.ui.languageTranslation

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ailab.databinding.FragmentLanguageTranslationBinding
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.fragment_language_translation.*
import java.util.*


class LanguageTranslationFragment : Fragment() {

    private var _binding: FragmentLanguageTranslationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var pDialog: SweetAlertDialog
    private lateinit var franchEngTranslator: Translator
    private var originalText:String=""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentLanguageTranslationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnFindLanguage.setOnClickListener {
            if(textTtoEdit.text.toString().equals("")){
                Toast.makeText(requireContext(),"Please entre text",Toast.LENGTH_LONG)
                    .show()
            }else{
                detectLang(textTtoEdit.text.toString())


            }
        }
        btnTranslate.setOnClickListener {
            originalText=textTtoEdit.text.toString()
            setUpPocessDialog()
            prepareTranslator()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun detectLang(txt:String){
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build())
        languageIdentifier.identifyLanguage(txt)
            .addOnSuccessListener {languageCode ->
                if(languageCode== "und") {
                    Log.i(TAG, "Can't identify language.")
                    Toast.makeText(requireContext(),"Can't identify language.",Toast.LENGTH_LONG)
                        .show()
                }
                else{
                    val loc = Locale(languageCode)
                    langCode.text="Language code : " +languageCode.toString().trim() +
                            "\n Language : "+loc.getDisplayLanguage(loc).toString().toUpperCase().trim()




                }

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"error: ${it.message}",Toast.LENGTH_LONG)
                    .show()
            }

    }


    private fun setUpPocessDialog() {
        pDialog= SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor= Color.parseColor("#FF03A9F4")
        pDialog.titleText="Loading"
        pDialog.setCancelable(false)
        pDialog.show()
    }

    private fun prepareTranslator() {
        val options : TranslatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.FRENCH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        franchEngTranslator= Translation.getClient(options)
        /* var conditions = DownloadConditions.Builder()
             .requireWifi()
             .build()*/
        pDialog.titleText="Downlooading Model ..."

        franchEngTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                pDialog.dismissWithAnimation()
                textTranslate()
            }
            .addOnFailureListener {
                txtRes.text="Error ${it.message}"
            }


    }
    private fun textTranslate() {
        pDialog.titleText="Translate text"
        pDialog.show()
        franchEngTranslator.translate(originalText)
            .addOnSuccessListener { translatedText ->
                pDialog.dismissWithAnimation()
                txtRes.text=translatedText
            }
            .addOnFailureListener { exception ->
                txtRes.text="Error ${exception.message}"
            }
    }

}