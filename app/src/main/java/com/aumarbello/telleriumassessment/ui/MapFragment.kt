package com.aumarbello.telleriumassessment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.aumarbello.telleriumassessment.di.ViewModelFactory
import com.aumarbello.telleriumassessment.models.UserLocation
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.utils.appComponent
import com.aumarbello.telleriumassessment.utils.updateToolbarTitle
import com.aumarbello.telleriumassessment.viewmodels.MapVM
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.*
import com.mapbox.mapboxsdk.utils.ColorUtils.colorToRgbaString
import javax.inject.Inject

class MapFragment : Fragment(R.layout.fragment_map) {
    @Inject
    lateinit var factory: ViewModelFactory

    private var currentLocation: Symbol? = null
    private lateinit var mode: String
    private lateinit var symbolManager: SymbolManager
    private lateinit var mapView: MapView

    private val viewModel by navGraphViewModels<MapVM>(R.id.user_details) { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent?.inject(this)

        mode = arguments?.getString(launchMode, null)
            ?: throw IllegalArgumentException("Failed to pass required coordinates argument")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToolbarTitle(R.string.label_map, true)

        mapView = view as MapView

        mapView.getMapAsync { map ->
            when (mode) {
                launchSingle, launchMultiple -> {
                    initializeSingleLocationMap(map)

                    map.addOnMapClickListener {
                        confirmLocation(it, mode)
                        true
                    }
                }

                launchDisplay -> {
                    val coordinates = arguments?.getParcelableArrayList<LatLng>(displayCoordinates)
                        ?: throw IllegalArgumentException("Failed to pass required coordinates argument")

                    map.setStyle(Style.MAPBOX_STREETS) {
                        val manager = FillManager(mapView, map, it)
                        manager.create(FillOptions().withLatLngs(listOf(coordinates)).withFillColor(colorToRgbaString(Color.BLACK)).withFillOpacity(0.6f))

                        val bounds = LatLngBounds.Builder().includes(coordinates).build()
                        map.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60), 600)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationReq && grantResults.first() == PackageManager.PERMISSION_GRANTED) {

            zoomMap()
        }
    }

    override fun onStart() {
        super.onStart()

        if (::mapView.isInitialized)
            mapView.onStart()
    }


    override fun onResume() {
        super.onResume()

        if (::mapView.isInitialized)
            mapView.onResume()
    }

    override fun onPause() {
        super.onPause()

        if (::mapView.isInitialized)
            mapView.onPause()
    }

    override fun onStop() {
        super.onStop()

        if (::mapView.isInitialized)
            mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (::mapView.isInitialized)
            mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        if (::mapView.isInitialized)
            mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (::mapView.isInitialized)
            mapView.onDestroy()
    }

    private fun initializeSingleLocationMap(map: MapboxMap) {
        map.setStyle(Style.MAPBOX_STREETS) {
            symbolManager = SymbolManager(mapView, map, it)
            val location = resources.getDrawable(R.drawable.ic_add_location, activity?.theme)
            it.addImage("location", location)
        }

        val locationPerm = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(requireContext(), locationPerm) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(locationPerm), locationReq)
            return
        }

        zoomMap()
    }

    @SuppressLint("MissingPermission")
    private fun zoomMap() {
        val client = LocationServices.getFusedLocationProviderClient(requireContext())
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                mapView.getMapAsync {
                    it.easeCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 12.0))
                }
            }
        }
    }

    private fun confirmLocation(location: LatLng, mode: String) {
        if (currentLocation != null) {
            currentLocation?.latLng = location
            symbolManager.update(currentLocation)
        } else {
            currentLocation = symbolManager.create(
                SymbolOptions().withLatLng(location)
                    .withIconImage("location")
                    .withIconSize(1.5f))
        }

        //display marker and snackbar with action
        val snackbar = Snackbar.make(requireView(), R.string.label_location_prompt, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.action_confirm) {
            val farmLocation = UserLocation(location.latitude, location.longitude)
            if (mode == launchSingle) {
                viewModel.setFarmLocation(farmLocation)
            } else {
                viewModel.addCoordinate(farmLocation)
            }

            snackbar.dismiss()
            findNavController().popBackStack()
        }
        snackbar.show()
    }

    companion object {
        const val launchMode = "com.aumarbello.farmlog.ui.launchMode"

        const val launchSingle = "farm-location"
        const val launchMultiple = "farm-coordinates"
        const val launchDisplay = "display farm coordinates"

        const val displayCoordinates = "farm-coordinates"

        private const val locationReq = 91
    }
}