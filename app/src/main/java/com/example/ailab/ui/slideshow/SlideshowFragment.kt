package com.example.ailab.ui.slideshow

import android.app.Activity
import android.content.ContentValues
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ailab.databinding.FragmentSlideshowBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.take_pic
import kotlinx.android.synthetic.main.fragment_slideshow.*
import java.lang.Exception

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val CAMERA_PERMISSION_CODE=123
    private val READ_STORAGE_PERMISSION_CODE=113
    private val WRITE_STORAGE_PERMISSION_CODE=113
    private val TAG="MyTag"
    private lateinit var cameraActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageInput: InputImage
    private lateinit var imageLabeler: ImageLabeler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // create camera launcher
        cameraActivityLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    val data=result?.data
                    try {
                        val photo =data?.extras?.get("data") as Bitmap
                        // img.setImageBitmap(photo)
                        imageInput= InputImage.fromBitmap(photo,0)
                        imageView.setImageBitmap(photo)
                        processImage()
                    }
                    catch (e: Exception){
                        Log.d(ContentValues.TAG,"onActivityResult ${e.message}")
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
                        imageView.setImageURI(data?.data)
                        processImage()
                        //FaceRec()
                    }
                    catch (e: Exception){
                        Log.d(ContentValues.TAG,"onActivityResult ${e.message}")
                    }
                }
            }
        )
        //get ai model to extract labels frrom image
        imageLabeler= ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        return root
    }

    private fun processImage() {
        imageLabeler.process(imageInput)
            .addOnSuccessListener { labels ->
                var res=""
                for (label in labels) {
                    res=res+"\n"+label.text
                }
                text_slideshow.text=res

            }
            .addOnFailureListener { e ->
                Log.d(TAG,"Image labeling ${e.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        checkPermission(android.Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        take_pic.setOnClickListener {
            val opts= arrayOf("Camera","Gallery")
            val builder= AlertDialog.Builder(requireContext())
            builder.setTitle("Choose option")
            builder.setItems(opts,
                DialogInterface.OnClickListener{
                        dialog,which ->
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