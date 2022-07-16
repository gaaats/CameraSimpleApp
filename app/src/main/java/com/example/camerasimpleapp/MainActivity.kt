package com.example.camerasimpleapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.camerasimpleapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private var tempImgUri: Uri? = null
    private var tempImgFilePath: String = ""

    private val simpleCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null){
            val bundle: Bundle = it.data!!.extras!!
            val bitmap = bundle.get("data") as Bitmap
            binding.imgPhoto.setImageBitmap(bitmap)
        }
    }

    // use ActivityResultContracts.PickContact()
    private val selectPictureLauncherAlbum =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Log.d("MY_TAG", "selectPictureLauncherAlbum GOOD")
                binding.imgPhoto.setImageURI(uri)
            } else {
                Log.d("MY_TAG", "selectPictureLauncherAlbum BAD")
            }
        }
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            Log.d("MY_TAG", "cameraLauncher GOOD")
            binding.imgPhoto.setImageURI(tempImgUri)
        } else {
            Log.d("MY_TAG", "cameraLauncher BAD")
        }
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        binding.btnMakePhoto.setOnClickListener {
            if (intentCamera.resolveActivity(packageManager) != null) {
                simpleCameraLauncher.launch(intentCamera)
            } else {
                Log.d("MY_TAG", "there is no app to resolve this intent")
            }
        }



//        binding.btnMakePhoto.setOnClickListener {
//            tempImgUri = FileProvider.getUriForFile(
//                this,
//                "com.example.camerasimpleapp.provide",
//                createImgFile().also {
//                    tempImgFilePath = it.absolutePath
//                })
//            cameraLauncher.launch(tempImgUri)
//
//
//        }
//
//        binding.btnAlbum.setOnClickListener {
//            selectPictureLauncherAlbum.launch("image/*")
//        }


    }

    private fun createImgFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 111
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}