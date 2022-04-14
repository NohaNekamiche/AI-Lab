package com.example.ailab.ui.gallery

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ailab.databinding.FragmentGalleryBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.lang.Exception

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var cameraActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryActivityLauncher:ActivityResultLauncher<Intent>
    lateinit var txt:TextView

    private lateinit var imageInput: InputImage
    lateinit var textRecognizer: TextRecognizer
    private val CAMERA_PERMISSION_CODE=123
    private val READ_STORAGE_PERMISSION_CODE=113
    private val WRITE_STORAGE_PERMISSION_CODE=113

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.result
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        // create camera launcher
        cameraActivityLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            object :ActivityResultCallback<ActivityResult>{
                override fun onActivityResult(result: ActivityResult?) {
                    val data=result?.data
                    try {
                        val photo =data?.extras?.get("data") as Bitmap
                       // img.setImageBitmap(photo)
                        imageInput= InputImage.fromBitmap(photo,0)
                        processImage()
                        //FaceRec()
                    }
                    catch (e: Exception){
                        Log.d(TAG,"onActivityResult ${e.message}")
                    }
                }
            }
        )
        //create gallery launcher
        galleryActivityLauncher= registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                object :ActivityResultCallback<ActivityResult>{
                    override fun onActivityResult(result: ActivityResult?) {
                        val data=result?.data
                        try {
                            imageInput= data?.data?.let {
                                InputImage.fromFilePath(requireContext(),
                                    it
                                )
                            }!!

                            processImage()
                            //FaceRec()
                        }
                        catch (e: Exception){
                            Log.d(TAG,"onActivityResult ${e.message}")
                        }
                    }
                }
            )


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //click btn take pic
        take_pic.setOnClickListener {
            val opts= arrayOf("Camera","Gallery")
            val builder=AlertDialog.Builder(requireContext())
            builder.setTitle("Choose option")
            builder.setItems(opts,
                DialogInterface.OnClickListener{
                        dialog, which ->
                    if(which==0){
                        val cameraIntent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraActivityLauncher?.launch(cameraIntent)
                    }else{
                        val storageIntent=Intent()
                        storageIntent.setType("image/*")
                        storageIntent.action=Intent.ACTION_GET_CONTENT
                        galleryActivityLauncher?.launch(storageIntent)
                    }
                })
            builder.show()
        }
    }

    private fun processImage() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        checkPermission(android.Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE)
    }
    //check phone permissions
    private fun checkPermission(permission:String,requestCode:Int){
        if(ContextCompat.checkSelfPermission(requireContext() ,permission)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(requireContext() as Activity, arrayOf(permission),requestCode)
        }
        else{
            Toast.makeText(requireContext() ,"permission already is garented ", Toast.LENGTH_LONG).show()
        }
    }

    //get permession
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_STORAGE_PERMISSION_CODE)
                Toast.makeText(requireContext() ,"permission already is garented ", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext() ,"permission already is denied ", Toast.LENGTH_LONG).show()
            }
        }
        else if(requestCode==READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    WRITE_STORAGE_PERMISSION_CODE)
                Toast.makeText(requireContext() ,"storage already is garented ", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext() ,"storage already is denied ", Toast.LENGTH_LONG).show()
            }
        }

        else if(requestCode==WRITE_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext() ,"storage already is garented ", Toast.LENGTH_LONG).show()
            }

        }
    }

}