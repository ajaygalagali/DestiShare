package com.astro.destishare.ui.homeFragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.astro.destishare.R
import com.astro.destishare.adapters.HomeAdapter
import com.astro.destishare.adapters.UserPostsAdapter
import com.astro.destishare.ui.FirestoreViewModel
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_done_post.*
import kotlinx.android.synthetic.main.fragment_home.*


class DonePostFragment : Fragment(R.layout.fragment_done_post) {
    private val TAG = "ActivePostFragment"

    lateinit var auth : FirebaseAuth
    lateinit var viewModel : FirestoreViewModel
    lateinit var dAdapter : UserPostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "onViewCreated: PhoneNumber -> ${auth.currentUser?.phoneNumber}")

        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        // Populating Recycler View
        viewModel.getUserDonePosts(auth.currentUser?.uid!!).observe(viewLifecycleOwner, Observer {
            dAdapter.differ.submitList(it)

        })

        // View on map
        dAdapter.setOnViewMapClickListener {
            val dlat = it.destLatLang.lat
            val dlng = it.destLatLang.lng
            val slng = it.spLatLang.lng
            val slat = it.spLatLang.lat



            val gmmIntentUri = if (dlat == -1.000 || slat == -1.000){
                // Since user manually typed locations, No coordinates, thus searching by name
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${it.startingPoint}&destination=${it.destination}")
            }else{
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$slat,$slng&destination=$dlat,$dlng")
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

    }

    private fun setupRecyclerView(){
        dAdapter = UserPostsAdapter()

        rvDonePosts.apply {
            adapter = dAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}