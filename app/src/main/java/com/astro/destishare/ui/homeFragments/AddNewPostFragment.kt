package com.astro.destishare.ui.homeFragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import com.astro.destishare.firestore.postsmodels.LatiLongi
import com.astro.destishare.firestore.postsmodels.PostsModel
import com.astro.destishare.notifications.FirebaseService
import com.astro.destishare.util.SecretKeys.Companion.MAPBOX_ACCESS_TOKEN
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_add_new_post.*
import kotlinx.android.synthetic.main.mapbox_search_bottomsheet.*
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Time
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class AddNewPostFragment : Fragment(R.layout.fragment_add_new_post) {

    private val TAG = "AddNewPostFragment"
    lateinit var auth : FirebaseAuth

    lateinit var bottomSheetBehavior : BottomSheetBehavior<ConstraintLayout>
    private lateinit var pickedDate : Date
    var pickedTime =""
    var timeInMillisecond = 0
    var dateInMillisecond = 0.toLong()
    var coordinatesStartingPoint : LatiLongi = LatiLongi(-1.000,-1.000)
    var coordinatesDestination : LatiLongi = LatiLongi(-1.000,-1.000)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialization of MapBox BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(clMapBoxBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        // NavigationIconClick
        toolbar_addNewPostFragment.setNavigationOnClickListener{

            findNavController().navigate(R.id.action_addNewPostFragment_to_homeFragment)

        }



        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore.collection("posts")
        val userId = auth.currentUser?.uid
        val displayName = auth.currentUser?.displayName


        etStartingPoint.setOnClickListener {
            getMapBoxSearch(savedInstanceState, "Enter Starting Point", false)
        }

        etDestination.setOnClickListener {
            getMapBoxSearch(savedInstanceState, "Enter Destination", true)
        }

        btnPickDate.setOnClickListener {
            pickDate()
        }

        btnPickTime.setOnClickListener {
            pickTime()
        }

        btnPostNewDestination.setOnClickListener {

            val startingPoint = etStartingPoint.text.toString()
            val destination = etDestination.text.toString()
            val peopleCount = etPeopleNumber.text.toString()
            var note = etNotePost.text.toString()
            if (note.isEmpty()){
                note = "Traveler hasn't left note"
            }
            val date = btnPickDate.text.toString()
            val time = btnPickTime.text.toString()

            if (startingPoint.isEmpty()){
                etStartingPoint.error = "Required"
            }else if (destination.isEmpty()){
                etDestination.error = "Required"
            }else if (peopleCount.isEmpty()){
                etPeopleNumber.error = "How many people's company are you looking for?"
            }else if (date == "Pick Date"){
                Snackbar.make(parentFragment?.view as View,"Which day are you planning to travel? - Pick Date",Snackbar.LENGTH_SHORT).show()

                Log.d(TAG, "onViewCreated: Pick Date")
            }else if (time == "Pick Time"){
                Snackbar.make(parentFragment?.view as View,"Let others know you are starting:) - Pick Time",Snackbar.LENGTH_SHORT).show()

                Log.d(TAG, "onViewCreated: Pick Time")
            }else {

                // Showing Loading State
                showProgressBarOne()
                hideLayout()

                // Creating unique Id
                val uuid = UUID.randomUUID().toString()

                // Generating Timestamp
                val now = Calendar.getInstance().time

                // DeadTime
               

                // Notification token
//                val token = FirebaseService.token

                // Creating PostModels object to upload to Firestore
                val newPost = PostsModel(
                    uuid,
                    userId!!,
                    displayName!!,
                    startingPoint,
                    coordinatesStartingPoint,
                    destination,
                    coordinatesDestination,
                    note,
                    Date(dateInMillisecond+timeInMillisecond),
                    peopleCount.toInt(),
                    now,
                    false
                )


                // Uploading to Firestore
                db.add(newPost)
                    .addOnCompleteListener {task->

                    if (task.isSuccessful){

                        /*Snackbar.make(parentFragment?.view as View,"You have DestiShare-d Successfully",Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.colorAccent))
                            .show()*/
                        findNavController().navigate(R.id.action_addNewPostFragment_to_homeFragment)

                    }else{

                        Snackbar.make(parentFragment?.view as View,"Something went wrong.. Try Again",Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.RED)
                            .show()

                        Log.d(TAG, "onViewCreated: FAILED POSTING")
                        // Showing UI
                        showLayout()
                        hideProgressBarOne()
                    }

                }
            }
        }

    }

    // MapBox Search Fragment
    private fun getMapBoxSearch(savedInstanceState: Bundle?, hint: String, isDestination: Boolean){

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val autocompleteFragment: PlaceAutocompleteFragment

        if (savedInstanceState == null) {

            val card = PlaceOptions.builder()
                .hint(hint)
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

                val geometry = carmenFeature.geometry()?.toJson()
                val jo = JSONObject(geometry!!)
                val ja = jo.get("coordinates") as JSONArray
                val coordinates = ArrayList<Double>()

                for (i in 0..1) {
                    coordinates.add(ja.get(i) as Double)
                }

                if (isDestination) {
                    etDestination.setText(carmenFeature.text().toString())
                    coordinatesDestination = LatiLongi(coordinates[0],coordinates[1])
                } else {
                    etStartingPoint.setText(carmenFeature.text().toString())
                    coordinatesStartingPoint = LatiLongi(coordinates[0],coordinates[1])
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onCancel() {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })
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