package com.example.camerasimpleapp

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.camerasimpleapp.databinding.ActivityMainBinding
import com.swein.easypermissionmanager.EasyPermissionManager
import java.io.File

class MainActivity : AppCompatActivity() {

    private var tempImgUri: Uri? = null
    private var tempImgFilePath: String = ""
    private var callBack: ((MutableMap<String, Boolean>) -> Unit)? = null
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")
    private val easyPermissionManager = EasyPermissionManager(this)

    private val registerPermissionCameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!it.values.contains(false)) {

            }
        }

    private val registerPermissionAlbumLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!it.values.contains(false)) {
                selectPictureLauncherAlbum.launch("image/*")
            }
        }


    private val selectPictureLauncherAlbum =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Log.d("MY_TAG", "selectPictureLauncherAlbum GOOD")
                binding.imgPhoto.setImageURI(uri)
            } else {
                Log.d("MY_TAG", "selectPictureLauncherAlbum BAD")
            }
        }
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isPhotoMade ->
            if (isPhotoMade) {
                Log.d("MY_TAG", "cameraLauncher GOOD")
                binding.imgPhoto.setImageURI(tempImgUri)
            } else {
                Log.d("MY_TAG", "cameraLauncher BAD")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMakePhoto.setOnClickListener {
            easyPermissionManager.requestPermission(
                "permission",
                "please CONFIRM permission",
                "confirm",
                arrayOf(Manifest.permission.CAMERA)
            ) {
                launchCamera()
            }


//            registerPermissionCameraLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
        binding.btnAlbum.setOnClickListener {


//            registerPermissionAlbumLauncher.launch(arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            ))
        }
    }
    private fun launchEasyManagerPermAndAddFunTodo (func: Runnable){

    }


//    private fun launchEasyManagerPermAndAddFunTodo(arrayPermissions: Array<String>, func: ()-> Unit) {
//        easyPermissionManager.requestPermission(
//            "permission",
//            "please CONFIRM permission",
//            "confirm",
//            arrayPermissions
//        ) {
//            launchCamera()
//        }
//    }

    private fun launchCamera() {
        tempImgUri = FileProvider.getUriForFile(
            this@MainActivity,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            createImgFile().also {
                tempImgFilePath = it.absolutePath
            })
        cameraLauncher.launch(tempImgUri)
    }

    private fun createImgFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", ".jpg", storageDir)
    }

//    private fun requestPermissions(vararg : String) {
    //single premissiom
//        registerPermissionLauncher.launch(Manifest.permission.CAMERA)

//        registerPermissionLauncher.launch(
//            arrayOf(vararg)
//                Manifest.permission.CAMERA,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//
//    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 111
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}


//    private val registerPermissionLauncher by lazy {
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//            if (null == it.values.find { it == false }) {
//                binding.btnMakePhoto.setOnClickListener {
//                    tempImgUri = FileProvider.getUriForFile(
//                        this@MainActivity,
//                        "${BuildConfig.APPLICATION_ID}.fileprovider",
//                        createImgFile().also {
//                            tempImgFilePath = it.absolutePath
//                        })
//                    cameraLauncher.launch(tempImgUri)
//                }
//
//                binding.btnAlbum.setOnClickListener {
//                    selectPictureLauncherAlbum.launch("image/*")
//                }
//                it.forEach {
//                    Log.d("MY_TAG", "${it.key} is: ${it.value}")
//                }
//            }
//        }
//    }


//    private val simpleCameraLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == RESULT_OK && it.data != null) {
//                val bundle: Bundle = it.data!!.extras!!
//                val bitmap = bundle.get("data") as Bitmap
//                binding.imgPhoto.setImageBitmap(bitmap)
//            }
//        }

// use ActivityResultContracts.PickContact()


//    @SuppressLint("QueryPermissionsNeeded")

//        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

//        binding.btnMakePhoto.setOnClickListener {
//            callBack = {
//                if (null == it.values.find { it == false }) {
//                    it.forEach {
//                        Log.d("MY_TAG", "${it.key} is: ${it.value}")
//                    }
//                    tempImgUri = FileProvider.getUriForFile(
//                        this@MainActivity,
//                        "${BuildConfig.APPLICATION_ID}.fileprovider",
//                        createImgFile().also {
//                            tempImgFilePath = it.absolutePath
//                        })
//                    cameraLauncher.launch(tempImgUri)
//                }
//            }
//            requestPermissions()

//        binding.btnMakePhoto.setOnClickListener {
//            requestPermissions()
//            if (intentCamera.resolveActivity(packageManager) != null) {
//                simpleCameraLauncher.launch(intentCamera)
//            } else {
//                Log.d("MY_TAG", "there is no app to resolve this intent")
//            }
//        }

//
//        binding.btnMakePhoto.setOnClickListener {
//
//            val job1 = lifecycleScope.launch {
//                registerPermissionLauncher.launch(
//                    arrayOf(
//                        Manifest.permission.CAMERA
//                    )
//                )
//            }
//            val job2 = lifecycleScope.launch {
//                tempImgUri = FileProvider.getUriForFile(
//                    this@MainActivity,
//                    "${BuildConfig.APPLICATION_ID}.fileprovider",
//                    createImgFile().also {
//                        tempImgFilePath = it.absolutePath
//                    })
//                cameraLauncher.launch(tempImgUri)
//            }
//            lifecycleScope.launch{
//                job1.join()
//                job2.join()
//            }
//
//        }

//        binding.btnAlbum.setOnClickListener {
//            val job1 = lifecycleScope.async {
//                registerPermissionLauncher.launch(
//                    arrayOf(
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    )
//                )
//            }
//            lifecycleScope.async {
//                job1.await()
//                selectPictureLauncherAlbum.launch("image/*")
//            }
//        }




