package com.astro.destishare.ui.homeFragments.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.astro.destishare.R
import com.astro.destishare.adapters.HomeAdapter
import com.astro.destishare.adapters.UserPostsAdapter
import com.astro.destishare.ui.FirestoreViewModel
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_active_post.*
import kotlinx.android.synthetic.main.fragment_home.*


class ActivePostFragment : Fragment(R.layout.fragment_active_post) {
    private val TAG = "ActivePostFragment"

    lateinit var auth : FirebaseAuth
    lateinit var viewModel : FirestoreViewModel
    lateinit var mAdapter : UserPostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "onViewCreated: PhoneNumber -> ${auth.currentUser?.phoneNumber}")

        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        // Populating Recycler View
        viewModel.getUserActivePosts(auth.currentUser?.uid!!).observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)

        })

    }

    private fun setupRecyclerView(){
        mAdapter = UserPostsAdapter()

        rvActivePosts.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}