package com.example.camerasimpleapp

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
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

    @SuppressLint("UnsafeOptInUsageError")
    private fun initCamera() {
        cameraProviderFuture.addListener({
            val processCameraProvider = cameraProviderFuture.get()
            // todo:for preview
//            val preview = Preview.Builder().build()
            // todo:for imageCapture
//            val imageCapture = ImageCapture.Builder().build()

            //задаємо приблизні параметри зобораження
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1024, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build()

            // обираємо камеру
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // додаємо обработчик подій
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)){
                val img: Image = it.image!!

//                val bitmap = mapperYUVtoRGB.translateYUV(img, this)
                val bitmap = mapperConverotrJava.translateYUV(img, this)

                // дізнаємося у *it* орієнтасію і ставимо у нашого превью таку ж саму
                binding.imgPhoto.rotation = it.imageInfo.rotationDegrees.toFloat()
                binding.imgPhoto.setImageBitmap(bitmap)
                img.close()
            }

            // головна фун тут, яка запускає все + прив'язка до життєвого сайкл
            processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)

        }, ContextCompat.getMainExecutor(this))

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}

