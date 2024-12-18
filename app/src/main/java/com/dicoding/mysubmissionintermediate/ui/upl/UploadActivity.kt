package com.dicoding.mysubmissionintermediate.ui.upl

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import com.dicoding.mysubmissionintermediate.databinding.ActivityUploadBinding
import com.dicoding.mysubmissionintermediate.ui.FactoryViewModel
import com.dicoding.mysubmissionintermediate.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<ViewModelUpload> {
        FactoryViewModel.getInstance(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.story_upload)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getCurrentLocation { latitude, longitude ->
                    lat = latitude
                    lon = longitude
                }
            } else {
                lat = null
                lon = null
            }
        }

        binding.btnGallery.setOnClickListener {
            galleryLaunch.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            cameraLaunch.launch(currentImageUri!!)
        }

        binding.buttonAdd.setOnClickListener {
            if (!binding.edAddDescription.text.isNullOrBlank() && currentImageUri != null) {
                currentImageUri?.let { uri ->
                    val imageFile = uriToFile(uri, this).reduceFileImage()
                    val desc = binding.edAddDescription.text.toString()

                    showLoading(true)

                    val requestBody = desc.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody =
                        MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

                    val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                    val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                    viewModel.storyUpload(multipartBody, requestBody, latRequestBody, lonRequestBody).observe(this) { response ->
                        when (response) {
                            is ResultCode.Error -> showToast(getString(R.string.failed_upload))
                            ResultCode.Loading -> showLoading(true)
                            is ResultCode.Success -> {
                                showToast(getString(R.string.success_upload))
                                runBlocking {
                                    delay(500)
                                }
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }
                }
            } else {
                showToast(getString(R.string.not_valid))
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@UploadActivity).apply {
                    setMessage(R.string.upload_confirmation)
                    setPositiveButton(R.string.done) { _, _ ->
                        val intent = Intent(this@UploadActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        })
    }

    private fun getCurrentLocation(onLocationResult: (lat: Double, lon: Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationResult(it.latitude, it.longitude)
            } ?: run {
                showToast(getString(R.string.not_found_location))
            }
        }
    }

    private val galleryLaunch =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                currentImageUri = uri
                imageShow()
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                imageShow()
            } else {
                currentImageUri = null
            }
        }

    private fun imageShow() {
        currentImageUri?.let { uri ->
            binding.imgPlaceholder.setImageURI(uri)
            binding.imgPlaceholder.background = null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            EspressoIdlingResource.increment()
        } else {
            EspressoIdlingResource.decrement()
        }
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun showToast(message: String) {
        showLoading(false)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
