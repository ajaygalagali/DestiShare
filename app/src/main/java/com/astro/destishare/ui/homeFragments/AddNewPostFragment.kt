package com.astro.destishare.ui.homeFragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.astro.destishare.R
import com.astro.destishare.util.SecretKeys.Companion.MAPBOX_ACCESS_TOKEN
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.JsonObject
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_add_new_post.*
import kotlinx.android.synthetic.main.mapbox_search_bottomsheet.*
import kotlinx.android.synthetic.main.otp_bottomsheet.*


class AddNewPostFragment : Fragment(R.layout.fragment_add_new_post) {

    private val TAG = "AddNewPostFragment"

    lateinit var bottomSheetBehavior : BottomSheetBehavior<ConstraintLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialization of MapBox BottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(clMapBoxBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        etStartingPoint.setOnClickListener {
            getMapBoxSearch(savedInstanceState,"Starting Point",false)
        }

        etDestination.setOnClickListener {
            getMapBoxSearch(savedInstanceState,"Destination",true)
        }

    }

    // MapBox Search Fragment
    private fun getMapBoxSearch(savedInstanceState: Bundle?, hint :String, isDestination : Boolean){

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val autocompleteFragment: PlaceAutocompleteFragment

        if (savedInstanceState == null) {

            val card = PlaceOptions.builder()
                .hint(hint)
                .build(PlaceOptions.MODE_CARDS)

            autocompleteFragment = PlaceAutocompleteFragment.newInstance(MAPBOX_ACCESS_TOKEN,card)

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.clMapBoxBottomSheet, autocompleteFragment, TAG)
            transaction.commit()

        } else {
            autocompleteFragment = requireActivity().supportFragmentManager.findFragmentByTag(TAG) as PlaceAutocompleteFragment
        }

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                Toast.makeText(requireContext(),
                    carmenFeature.text(), Toast.LENGTH_LONG).show()

                if (isDestination){
                    etDestination.setText(carmenFeature.text().toString())
                }else{
                    etStartingPoint.setText(carmenFeature.text().toString())

                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


            }

            override fun onCancel() {

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            }
        })


    }

}