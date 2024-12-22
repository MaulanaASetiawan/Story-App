package com.example.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.helper.getImageUri
import com.example.storyapp.helper.reduceFileImage
import com.example.storyapp.helper.uriToFile
import com.example.storyapp.data.Result
import com.example.storyapp.ui.home.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var lat: Double? = null
    private var lon: Double? = null

    private val requestPermissionLauncher =  registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.addStoryImage.setImageURI(uri)
            viewModel.setCurrentImgUri(uri)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.addStoryImage.setImageURI(viewModel.currentImageUri.value)
        } else {
            viewModel.setCurrentImgUri(null)
        }
    }

    private fun startCamera(){
            viewModel.setCurrentImgUri(getImageUri(this))
            viewModel.currentImageUri.value?.let { launcherCamera.launch(it) }
    }

    private fun showImage() {
        viewModel.currentImageUri.let {
            Log.d("Image URI", "showImage: $it")
            binding.addStoryImage.setImageURI(it.value)
        }
    }

    private fun uploadStory() {
        val uri = viewModel.currentImageUri.value
        if (uri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.addDescription.text.toString()
        if (description.isBlank()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }
        val imageFile = uriToFile(uri, this)
        val reducedImageFile = imageFile.reduceFileImage()

        val lat = if (lat != null && !lat!!.isNaN()) lat else null
        val lon = if (lon != null && !lon!!.isNaN()) lon else null

        viewModel.uploadStory(reducedImageFile, description, lat!!, lon!!).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    Snackbar.make(binding.root, "Story uploaded", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { data: Location? ->
                if (data != null) {
                    lat = data.latitude
                    lon = data.longitude
                    Toast.makeText(this, R.string.location_succes, Toast.LENGTH_SHORT).show()
                    Log.d("Location", "getLocation: $lat, $lon")
                } else {
                    Toast.makeText(this, R.string.location_failed, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        supportActionBar?.hide()

        if(!allPermissionsGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.locationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
            } else {
                lat = null
                lon = null
            }
        }

        viewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            }
        }

        binding.cameraButton.setOnClickListener { startCamera() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadStory() }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}