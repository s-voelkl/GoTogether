package com.goTogether_android.challenges

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.goTogether_android.R
import com.goTogether_android.data.ChallengeRepository
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CheckInBottomSheet : BottomSheetDialogFragment() {

    private lateinit var scanArea: View
    private lateinit var manualArea: View
    private lateinit var successArea: View
    private lateinit var subtitle: TextView
    private lateinit var previewView: PreviewView
    private lateinit var cameraPlaceholder: View

    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottomsheet_checkin, container, false)

        scanArea = v.findViewById(R.id.scanArea)
        manualArea = v.findViewById(R.id.manualArea)
        successArea = v.findViewById(R.id.successArea)
        subtitle = v.findViewById(R.id.checkInSubtitle)
        previewView = v.findViewById(R.id.previewView)
        cameraPlaceholder = v.findViewById(R.id.cameraPlaceholder)

        cameraExecutor = Executors.newSingleThreadExecutor()

        v.findViewById<Button>(R.id.enterCodeBtn).setOnClickListener {
            switchToManual()
        }

        v.findViewById<Button>(R.id.backToScanBtn).setOnClickListener {
            switchToScan()
        }

        val codeInput = v.findViewById<EditText>(R.id.codeInput)
        v.findViewById<Button>(R.id.submitCodeBtn).setOnClickListener {
            val code = codeInput.text.toString()
            handleCode(code, codeInput)
        }

        v.findViewById<Button>(R.id.doneBtn).setOnClickListener {
            dismiss()
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
        }

        return v
    }

    private fun handleCode(code: String, errorTarget: EditText? = null) {
        val challenge = ChallengeRepository.findByVerificationCode(code)
        if (challenge != null) {
            view?.findViewById<TextView>(R.id.matchedChallengeName)?.text = challenge.name
            view?.findViewById<TextView>(R.id.matchedChallengePoints)?.text = "+${challenge.points} pts"
            switchToSuccess()
        } else {
            errorTarget?.setError("Invalid code")
            if (errorTarget == null) {
                Toast.makeText(requireContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val barcodeScanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            )

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
                cameraPlaceholder.visibility = View.GONE
            } catch (exc: Exception) {
                Toast.makeText(requireContext(), "Use manual entry", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { code ->
                            activity?.runOnUiThread {
                                handleCode(code)
                            }
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchToManual() {
        scanArea.visibility = View.GONE
        manualArea.visibility = View.VISIBLE
        successArea.visibility = View.GONE
        subtitle.text = "Enter the 5-character code\nshown at the challenge venue"
    }

    private fun switchToScan() {
        scanArea.visibility = View.VISIBLE
        manualArea.visibility = View.GONE
        successArea.visibility = View.GONE
        subtitle.text = "Point your camera at the QR code\nshown at the challenge venue"
    }

    private fun switchToSuccess() {
        scanArea.visibility = View.GONE
        manualArea.visibility = View.GONE
        successArea.visibility = View.VISIBLE
        subtitle.visibility = View.GONE
        cameraProvider?.unbindAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
