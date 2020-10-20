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
import com.astro.destishare.adapters.UserPostsAdapter
import com.astro.destishare.ui.FirestoreViewModel
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_active_post.*
import kotlinx.android.synthetic.main.fragment_joined.*


class JoinedFragment : Fragment(R.layout.fragment_joined) {
    private val TAG = "ActivePostFragment"

    lateinit var auth : FirebaseAuth
    lateinit var viewModel : FirestoreViewModel
    lateinit var mAdapter : UserPostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        // Populating Recycler View
        viewModel.getJoinedPosts(auth.currentUser?.uid!!).observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)

        })

    }

    private fun setupRecyclerView(){
        mAdapter = UserPostsAdapter()

        rvJoinedFragment.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}