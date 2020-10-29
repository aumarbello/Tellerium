package com.aumarbello.telleriumassessment.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.databinding.FragmentUserDetailsBinding
import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.di.ViewModelFactory
import com.aumarbello.telleriumassessment.models.UserLocation
import com.aumarbello.telleriumassessment.utils.*
import com.aumarbello.telleriumassessment.viewmodels.MapVM
import com.aumarbello.telleriumassessment.viewmodels.UserDetailsVM
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialContainerTransform
import com.mapbox.mapboxsdk.geometry.LatLng
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserDetailsFragment : Fragment(R.layout.fragment_user_details) {
    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var coordinatesAdapter: CoordinatesAdapter

    private lateinit var launchMode: String
    private lateinit var sharedViewModel: MapVM
    private lateinit var currentImagePath: String

    private val viewModel by viewModels<UserDetailsVM> { factory }
    private val args by navArgs<UserDetailsFragmentArgs>()

    private var isFilled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent?.inject(this)
        viewModel.getUser(args.userId)

        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentUserDetailsBinding.bind(view)
        updateToolbarTitle(R.string.label_new_farm, true)

        //Workaround fix for null backStackEntry when testing with mock navController
        sharedViewModel = try {
            ViewModelProvider(
                findNavController().getBackStackEntry(R.id.user_details).viewModelStore,
                factory
            )[MapVM::class.java]
        } catch (ex: IllegalStateException) {
            ViewModelProvider(this, factory)[MapVM::class.java]
        }

        coordinatesAdapter = CoordinatesAdapter(sharedViewModel::removeCoordinate)

        val spacing = resources.getDimension(R.dimen.spacing_tiny)
        binding.coordinateList.also {
            it.adapter = coordinatesAdapter
            it.addItemDecoration(GridSpacingDecorator(spacing.toInt()))
        }

        setObservers()
        setListeners()

        startTransitions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imageReq && resultCode == Activity.RESULT_OK) {
            sharedViewModel.setImagePath(currentImagePath)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationReq && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            pickCurrentLocation(launchMode)
        } else {
            showSnackBar("Permission denied, kindly pick location on map")
            openMap(launchMode)
        }
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            binding.loader.fadeView(it.loading)

            it.error?.getContentIfNotHandled()?.let { msg -> showSnackBar(msg) }

            if (it.data != null && !isFilled) {
                isFilled = true
                preFillDetails(it.data)
            }
        })

        sharedViewModel.imagePath.observe(viewLifecycleOwner, Observer {
            binding.profileImage.loadImage(it)
        })

        sharedViewModel.coordinates.observe(viewLifecycleOwner, Observer {
            coordinatesAdapter.submitList(it)

            binding.viewOnMap.isVisible = it.size >= 3
        })

        sharedViewModel.farmLocation.observe(viewLifecycleOwner, Observer {
            binding.farmLocation.text =
                getString(R.string.format_location, it.latitude, it.longitude)
        })
    }

    private fun preFillDetails(data: UserEntity) {
        sharedViewModel.setImagePath(data.imageUrl)
        binding.firstName.setText(data.firstName)
        binding.otherNames.setText(data.lastName)
        binding.phoneNumber.setText(data.phoneNumber)
        binding.age.setText( data.dateOfBirth)

        val selected = if (data.gender.equals("Male", true)) {
            R.id.male
        } else
            R.id.female
        binding.genderGroup.check(selected)
        binding.farmName.setText(data.farmName)
        data.location?.let { sharedViewModel.setFarmLocation(it) }
        data.farmCoordinates.forEach { sharedViewModel.addCoordinate(it) }
    }

    private fun setListeners() {
        binding.save.setOnClickListener {
            val details = validate()
            if (details != null) {
                viewModel.updateUser(details)
                findNavController().popBackStack()
            }
        }

        binding.profileImage.setOnClickListener {
            takePicture()
        }

        binding.addCoordinate.setOnClickListener {
            showOptionsDialog(MapFragment.launchMultiple)
        }

        binding.farmLocationLayout.setOnClickListener {
            showOptionsDialog(MapFragment.launchSingle)
        }

        binding.viewOnMap.setOnClickListener {
            val coordinates = (sharedViewModel.coordinates.value
                ?: return@setOnClickListener).map { LatLng(it.latitude, it.longitude) }
            val args = Bundle().apply {
                putString(MapFragment.launchMode, MapFragment.launchDisplay)
                putParcelableArrayList(MapFragment.displayCoordinates, ArrayList(coordinates))
            }

            findNavController().navigate(R.id.mapFragment, args)
        }

        binding.ageLayout.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val currentDate = binding.age.text.split("-").map { it.toInt() }

        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            binding.age.setText(getString(
                R.string.format_date_of_birth,
                year,
                month.inc(),
                day
            ))
        }

        DatePickerDialog(
            requireContext(),
            listener,
            currentDate[0],
            currentDate[1].dec(),
            currentDate[2]
        ).show()
    }

    private fun validate(): UserEntity? {
        if (!validateInputLayout(binding.firstName, binding.firstNameLayout,
                R.string.error_first_name) { it.length >= 3 }
        ) {
            return null
        }

        if (!validateInputLayout(binding.otherNames, binding.otherNameLayout,
                R.string.error_other_names) { it.length >= 3 }
        ) {
            return null
        }

        val phoneNumberPattern = Pattern.compile("[+]?[(]?\\d{3}[)]?\\d{3}\\d{4,7}")
        if (!validateInputLayout(
                binding.phoneNumber, binding.phoneNumberLayout,
                R.string.error_phone_number
            ) {
                phoneNumberPattern.matcher(it).matches()
            }
        ) {
            return null
        }

        val dob = binding.age.text.toString()
        val year = dob.split("-").first().toInt()
        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        if ((currentYear - year) < 18) {
            showSnackBar(R.string.error_age)
            return null
        }

        val selectedGender = binding.genderGroup.checkedRadioButtonId
        if (selectedGender == -1) {
            showSnackBar(R.string.error_gender)
            return null
        }

        if (!validateInputLayout(binding.farmName, binding.farmNameLayout,
                R.string.error_farm_name) { it.length >= 3 }
        ) {
            return null
        }

        if (sharedViewModel.farmLocation.value == null) {
            showSnackBar(R.string.error_farm_location)
            return null
        }

        if (!binding.viewOnMap.isVisible) {
            showSnackBar(R.string.error_coordinates)
            return null
        }

        val currentValue = viewModel.state.value?.data ?: return null
        return currentValue.copy(
            firstName = binding.firstName.text.toString(),
            lastName = binding.otherNames.text.toString(),
            phoneNumber = binding.phoneNumber.text.toString(),
            farmName = binding.farmName.text.toString(),
            gender = getSelectedGender(),
            farmCoordinates = sharedViewModel.coordinates.value!!,
            location = sharedViewModel.farmLocation.value!!,
            dateOfBirth = binding.age.text.toString(),
            imageUrl = sharedViewModel.imagePath.value!!
        )
    }

    private fun validateInputLayout(
        input: TextInputEditText,
        layout: TextInputLayout,
        @StringRes errorMessage: Int,
        validator: (String) -> Boolean
    ): Boolean {
        val text = input.text?.toString()

        if (text.isNullOrEmpty() || !validator(text)) {
            layout.error = getString(errorMessage)
            addTextWatcher(input, layout, validator)
            return false
        }

        return true
    }

    private fun addTextWatcher(
        input: TextInputEditText,
        layout: TextInputLayout,
        validator: (String) -> Boolean
    ) {
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (validator(s.toString())) {
                    input.removeTextChangedListener(this)

                    layout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getSelectedGender(): String {
        return if (binding.genderGroup.checkedRadioButtonId == R.id.male)
            "Male"
        else
            "Female"
    }

    private fun showOptionsDialog(launchMode: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.label_dialog_title)
            .setPositiveButton(R.string.action_current_location) { dialog, _ ->
                dialog.dismiss()

                if (GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS
                ) {
                    pickCurrentLocation(launchMode)
                } else {
                    showSnackBar("Locations service is unavailable, kindly pick location on map")

                    openMap(launchMode)
                }
            }
            .setNegativeButton(R.string.action_pick_on_map) { dialog, _ ->
                dialog.dismiss()

                openMap(launchMode)
            }.show()
    }

    private fun pickCurrentLocation(launchMode: String) {
        val locationPerm = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                locationPerm
            ) == PackageManager.PERMISSION_DENIED
        ) {
            this.launchMode = launchMode
            requestPermissions(arrayOf(locationPerm), locationReq)
            return
        }

        val client = LocationServices.getFusedLocationProviderClient(requireContext())
        client.lastLocation.addOnSuccessListener {
            if (it == null) {
                locationRetrievalFailed(launchMode)
                return@addOnSuccessListener
            }

            if (launchMode == MapFragment.launchSingle) {
                sharedViewModel.setFarmLocation(UserLocation(it.latitude, it.longitude))
            } else {
                sharedViewModel.addCoordinate(UserLocation(it.latitude, it.longitude))
            }
        }.addOnFailureListener {
            locationRetrievalFailed(launchMode)
        }
    }

    private fun locationRetrievalFailed(launchMode: String) {
        showSnackBar("Failed to retrieve current location, kindly pick location on map")
        openMap(launchMode)
    }

    private fun openMap(launchMode: String) {
        val args = Bundle().apply {
            putString(MapFragment.launchMode, launchMode)
        }
        findNavController().navigate(R.id.mapFragment, args)
    }

    private fun takePicture() {
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

        val imageFile = File.createTempFile("Avatar_$timeStamp", ".jpg", dir).apply {
            currentImagePath = absolutePath
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireContext().packageManager)?.also {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.aumarbello.telleriumassessment.fileprovider",
                    imageFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, imageReq)
            }
        }
    }

    private fun startTransitions() {
        enterTransition = MaterialContainerTransform(requireContext()).apply {
            endView = binding.root
        }

        returnTransition = Slide().apply {
            duration = 180
        }

        startPostponedEnterTransition()
    }

    private companion object {
        private const val locationReq = 11
        private const val imageReq = 21
    }
}