package com.example.camerasimpleapp

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.camerasimpleapp.databinding.ActivityMainBinding
import com.swein.easypermissionmanager.EasyPermissionManager

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")
    private val easyPermissionManager = EasyPermissionManager(this)
    private val cameraProviderFuture by lazy {
        ProcessCameraProvider.getInstance(this)
    }
    private val mapperYUVtoRGB = YUVtoRGB()
    private val mapperConverotrJava = Convertor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        easyPermissionManager.requestPermission(
            "Permission Camera",
            "i need permission Camera",
            "accept",
            arrayOf(android.Manifest.permission.CAMERA)
        ) {
            initCamera()
        }
    }

    private fun initCamera() {
        cameraProviderFuture.addListener({

            // procces here
            val processCameraProvider = cameraProviderFuture.get()

            // for preview
            val preview = Preview.Builder().build().also { preview: Preview ->
                preview.setSurfaceProvider(binding.previvCamera.surfaceProvider)
            }
            // for imageCapture
            val imageCapture = ImageCapture.Builder().build()

            // обираємо камеру
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // головна фун тут, яка запускає все + прив'язка до життєвого сайкл
            try {
                processCameraProvider.unbindAll()
                processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d("GATS", "exception ---- in initCamera, ${e.message.toString()}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}

