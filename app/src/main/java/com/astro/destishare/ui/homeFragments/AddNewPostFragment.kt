package com.astro.destishare.ui.homeFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import com.astro.destishare.models.firestore.postsmodels.LatiLongi
import com.astro.destishare.models.firestore.postsmodels.PostsModel
import com.astro.destishare.util.SecretKeys.Companion.MAPBOX_ACCESS_TOKEN
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.android.synthetic.main.fragment_add_new_post.*
import kotlinx.android.synthetic.main.mapbox_search_bottomsheet.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddNewPostFragment : Fragment(R.layout.fragment_add_new_post) {

    private val TAG = "AddNewPostFragment"
    private val LOCATION_REQUEST_CODE = 111

    lateinit var auth : FirebaseAuth

    lateinit var bottomSheetBehavior : BottomSheetBehavior<ConstraintLayout>
    private lateinit var pickedDate : Date
    var pickedTime =""
    var timeInMillisecond = 0
    var dateInMillisecond = 0.toLong()
    var coordinatesStartingPoint : LatiLongi = LatiLongi(-1.000,-1.000)
    var coordinatesDestination : LatiLongi = LatiLongi(-1.000,-1.000)

    var currentLocation : LatLng = LatLng(12.9716, 77.5946)

    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            if (result == null){
                return
            }
            for(location in result.locations){
                Log.d(TAG, "onLocationResult: ${location.toString()}")
                currentLocation = LatLng(location.latitude,location.longitude)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Location Request
        locationRequest = LocationRequest()
            .setInterval(4000)
            .setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Initialization of MapBox BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(clMapBoxBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        // NavigationIconClick
        toolbar_addNewPostFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_addNewPostFragment_to_homeFragment)
        }


        // User details
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        val displayName = auth.currentUser?.displayName

        // Posts reference
        val db = Firebase.firestore.collection("posts")

        // Mapbox PlacePicker
        Mapbox.getInstance(requireContext(), MAPBOX_ACCESS_TOKEN)

        // StartingPoint EditText
        etStartingPoint.setOnClickListener {
            getMapBoxSearch(savedInstanceState, "Enter Starting Point", false)
        }

        // Destination EditText
        etDestination.setOnClickListener {
            getMapBoxSearch(savedInstanceState, "Enter Destination", true)
        }

        // PickDate Button
        btnPickDate.setOnClickListener {
            pickDate()
        }

        // PickTime Button
        btnPickTime.setOnClickListener {
            pickTime()
        }

        // PlacePick for Starting Point
        ibPickOnMapStartingPoint.setOnClickListener {
            placePicker(0,currentLocation)
        }

        // PlacePick for Destination
        ibPickOnMapDesination.setOnClickListener {
            placePicker(1,currentLocation)
        }


        // Post Button
        btnPostNewDestination.setOnClickListener {

            val startingPoint = etStartingPoint.text.toString()
                .toLowerCase().replace(","," ").split(" ")

            val destination = etDestination.text.toString()
                .toLowerCase().replace(","," ").split(" ")
            val peopleCount = etPeopleNumber.text.toString()
            var note = etNotePost.text.toString()
            if (note.isEmpty()){
                note = "Traveler hasn't left note"
            }
            val date = btnPickDate.text.toString()
            val time = btnPickTime.text.toString()

            when {
                startingPoint.isEmpty() -> {
                    etStartingPoint.error = "Required"
                }
                destination.isEmpty() -> {
                    etDestination.error = "Required"
                }
                peopleCount.isEmpty() -> {
                    etPeopleNumber.error = "How many people's company are you looking for?"
                }
                date == "Pick Date" -> {
                    Snackbar.make(parentFragment?.view as View,"Which day are you planning to travel? - Pick Date",Snackbar.LENGTH_SHORT).show()
                }
                time == "Pick Time" -> {
                    Snackbar.make(parentFragment?.view as View,"Let others know you are starting:) - Pick Time",Snackbar.LENGTH_SHORT).show()
                }
                else -> {

                    // Showing Loading State
                    showProgressBarOne()
                    hideLayout()

                    // Generating Timestamp
                    val now = Calendar.getInstance().time

                    // Post Reference
                    val postRef = db.document()

                    // Creating PostModels object to upload to Firestore
                    val newPost = PostsModel(
                        postRef.id,
                        userId!!,
                        displayName!!,
                        startingPoint,
                        coordinatesStartingPoint,
                        destination,
                        coordinatesDestination,
                        note,
                        Date(dateInMillisecond+timeInMillisecond),
                        peopleCount.toInt(),
                        now
                    )

                    Log.d(TAG, "onViewCreated: StaringPt Coordinates -> $coordinatesStartingPoint")
                    Log.d(TAG, "onViewCreated: Destination Coordinates -> $coordinatesDestination")


                    // Uploading to Firestore
                    postRef.set(newPost)
                        .addOnCompleteListener {task->

                            if (task.isSuccessful){

                                Snackbar.make(parentFragment?.view as View,"You have DestiShare-d Successfully",Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.colorAccent))
                                    .show()
                                findNavController().navigate(R.id.action_addNewPostFragment_to_homeFragment)

                            }else{

                                Snackbar.make(parentFragment?.view as View,"Something went wrong.. Try Again",Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(Color.RED)
                                    .show()

                                // Showing UI
                                showLayout()
                                hideProgressBarOne()
                            }
                        }
                }
            }
        }

    }

    private fun checkSettingsAndStartLocationUpdates(){
        val request = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)
            .build()

        val client = LocationServices.getSettingsClient(requireContext())

        val locationSettingsRequest = client.checkLocationSettings(request)
            .addOnSuccessListener {
                Log.d(TAG, "checkSettingsAndStartLocationUpdates: SUCCESS")
                startLocationUpdates()
            }
            .addOnFailureListener {
                Log.d(TAG, "checkSettingsAndStartLocationUpdates: FAILED -> ${it.message}")
                if (it is ResolvableApiException){
                    val apiException = it as ResolvableApiException
                    apiException.startResolutionForResult(requireActivity(),1001)
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                checkSettingsAndStartLocationUpdates()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            checkSettingsAndStartLocationUpdates()
        }else{
            askLocationPermission()
        }

    }

    override fun onStop() {
        super.onStop()

        stopLocationUpdates()

    }



    // MapBox Search Fragment
    private fun getMapBoxSearch(savedInstanceState: Bundle?, hint: String, isDestination: Boolean){

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val autocompleteFragment: PlaceAutocompleteFragment

        if (savedInstanceState == null) {

            val card = PlaceOptions.builder()
                .hint(hint)
                .country("IN")
                .build(PlaceOptions.MODE_CARDS)

            autocompleteFragment = PlaceAutocompleteFragment.newInstance(MAPBOX_ACCESS_TOKEN, card)

            val transaction = requireActivity()
                .supportFragmentManager.beginTransaction()

            transaction.add(R.id.clMapBoxBottomSheet, autocompleteFragment, TAG)
            transaction.commit()

        } else {
            autocompleteFragment = requireActivity()
                .supportFragmentManager.findFragmentByTag(TAG) as PlaceAutocompleteFragment
        }

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {

                val coordinates = giveMeCoordinates(carmenFeature)

                if (isDestination) {
                    etDestination.setText(carmenFeature.text().toString())
                    coordinatesDestination = LatiLongi(coordinates[1],coordinates[0])
                } else {
                    etStartingPoint.setText(carmenFeature.text().toString())
                    coordinatesStartingPoint = LatiLongi(coordinates[1],coordinates[0])
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onCancel() {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })
    }


    // PlacePicker
    private fun placePicker(requestCode: Int,currentLocation : LatLng?){

        val intent = PlacePicker.IntentBuilder()
            .accessToken(Mapbox.getAccessToken()!!)
            .placeOptions(
                PlacePickerOptions.builder()
                    .statingCameraPosition(
                        CameraPosition.Builder()
                            .target(currentLocation)
                            .zoom(10.0)
                            .build())
                    .build())
            .build(requireActivity())
        startActivityForResult(intent, requestCode)

    }


    // PlacePicker Results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        // requestCode = 0 for StartingPoint
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val carmenFeature = PlacePicker.getPlace(data)!!
            etStartingPoint.setText(giveMeLocation(carmenFeature))
            val coordinates = giveMeCoordinates(carmenFeature)
            coordinatesStartingPoint = LatiLongi(coordinates[1],coordinates[0])

            // requestCode = 1 for Destination
        }else if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            val carmenFeature = PlacePicker.getPlace(data)!!
            etDestination.setText(giveMeLocation(carmenFeature))
            val coordinates = giveMeCoordinates(carmenFeature)
            coordinatesDestination = LatiLongi(coordinates[1],coordinates[0])
        }


    }

    // Converts CarmenFeature to Location
    private fun giveMeLocation(carmenFeature: CarmenFeature) : String{
        val carmenContext = carmenFeature.context()
        val nagar = carmenContext?.get(0)?.text()
        val city = carmenContext?.get(1)?.text()

        return "$nagar, $city"
    }


    // Converts CarmenFeature to Coordinates
    private fun giveMeCoordinates(carmenFeature: CarmenFeature): ArrayList<Double> {

        val geometry = carmenFeature.geometry()?.toJson()
        val jo = JSONObject(geometry!!)
        val ja = jo.get("coordinates") as JSONArray
        val coordinates = ArrayList<Double>()

        for (i in 0..1) {
            coordinates.add(ja.get(i) as Double)
        }

        return coordinates

    }

    // Date Picker
    private fun pickDate(){


        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val bounds = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
        val mDatePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText("Date of starting")
            setSelection(today)
            setCalendarConstraints(bounds)
        }.build()

        mDatePicker.show(parentFragmentManager, "Date_picker")

        mDatePicker.addOnPositiveButtonClickListener {
            pickedDate = Date(it)
            dateInMillisecond = it - ((5*60*60*1000)+(30*60*1000))
            val df = DateFormat.getDateInstance(DateFormat.MEDIUM)
            btnPickDate.text = df.format(pickedDate)
        }
    }

    // Time Picker
    private fun pickTime(){

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Time of starting")
            .build()

        materialTimePicker
            .addOnPositiveButtonClickListener {
                val hr = materialTimePicker.hour
                val min = materialTimePicker.minute
                timeInMillisecond = (hr*60*60*1000) + (min*60*1000)

            pickedTime = "${materialTimePicker.hour} : ${materialTimePicker.minute}"
            btnPickTime.text = pickedTime



        }
        materialTimePicker.show(parentFragmentManager, "AddNewPostFragment")


    }

    private fun showProgressBarOne(){
        clLoadingPosting.visibility = View.VISIBLE
    }

    private fun hideProgressBarOne(){
        clLoadingPosting.visibility = View.INVISIBLE
    }

    private fun showLayout(){
        clAddNewPostLayout.visibility = View.VISIBLE
    }

    private fun hideLayout(){
        clAddNewPostLayout.visibility = View.INVISIBLE
    }






}