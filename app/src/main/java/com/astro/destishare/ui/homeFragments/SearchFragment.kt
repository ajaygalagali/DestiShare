package com.astro.destishare.ui.homeFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.astro.destishare.R
import com.astro.destishare.adapters.HomeAdapter
import com.astro.destishare.ui.activities.HomeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.otp_bottomsheet.*
import kotlinx.android.synthetic.main.search_bottomsheet.*


class SearchFragment : Fragment(R.layout.fragment_search){

    private  val TAG = "SearchFragment"
    lateinit var mAdapter : HomeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Auth
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid!!

        // BottomSheet Behaviour
        val bottomSheetBehavior = BottomSheetBehavior.from(constraintLayoutSearchBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // BottomSheet Callback
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) { }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                ivArrowSearchBottomSheeet.rotation = ((slideOffset-1)*180)
            }
        })

        // Toggle BottomSheet
        constraintLayoutSearchBottomSheet.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.state  = BottomSheetBehavior.STATE_COLLAPSED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        // Back Arrow click
        toolbarSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        // Setting Up RecyclerView
        setupRecyclerView()

        // ViewModel
        val viewModel = (activity as HomeActivity).viewModel

        // Observing data
        viewModel.searchItemsLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)
            mAdapter.differFilter.submitList(it)
            if (it.isEmpty()){
                tvSearchFragmentNotify.text = "No results found!"
            }
            mAdapter.notifyDataSetChanged()
        })

        // Observing Loading State
        viewModel.loadingStateSearchFragment.observe(viewLifecycleOwner,{isLoading->
            if (isLoading){
                showProgressBar()
            }else{
                hideProgressBar()
            }
        })


        // Search Button Click
        btnSearch.setOnClickListener {
            val startingPoint = etStartingPointSearch.text.toString().toLowerCase().split(" ")
            val destination = etDestinationSearch.text.toString().toLowerCase()
            
            if (etStartingPointSearch.text.toString().isEmpty()){
                etStartingPointSearch.error = "Where do your journey begins from?"
            }

            if (destination.isEmpty()){
                etDestinationSearch.error = "Destination Please!"
            }

            if (etStartingPointSearch.text.toString().isNotEmpty() && destination.isNotEmpty()){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                viewModel.searchPost(startingPoint,destination,userId)
            }

        }

    }

    private fun setupRecyclerView(){
        mAdapter = HomeAdapter()

        rvSearchFragment.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showProgressBar(){
        progressBarSearchFragment.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBarSearchFragment.visibility = View.INVISIBLE
    }






}