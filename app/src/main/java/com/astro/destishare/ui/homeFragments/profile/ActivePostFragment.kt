package com.astro.destishare.ui.homeFragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.astro.destishare.R
import com.astro.destishare.adapters.UserPostsAdapter
import com.astro.destishare.ui.viewmodels.FirestoreViewModel
import com.astro.destishare.ui.activities.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_active_post.*


class ActivePostFragment : Fragment(R.layout.fragment_active_post) {
    private val TAG = "ActivePostFragment"

    lateinit var auth : FirebaseAuth
    lateinit var viewModel : FirestoreViewModel
    lateinit var mAdapter : UserPostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        viewModel = (activity as HomeActivity).viewModel

        // Populating Recycler View
        viewModel.getUserActivePosts(auth.currentUser?.uid!!)
            viewModel.userPostsActive
            .observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)

                if (it.isEmpty()){
                    tvActivePostsNotify.visibility = View.VISIBLE
                }else{
                    tvActivePostsNotify.visibility = View.INVISIBLE
                }

        })

        // View on map
        mAdapter.setOnViewMapClickListener {
            val dlat = it.destLatLang.lat
            val dlng = it.destLatLang.lng
            val slng = it.spLatLang.lng
            val slat = it.spLatLang.lat


            val gmmIntentUri = if (dlat == -1.000 || slat == -1.000){
                // Since user manually typed locations, No coordinates, thus searching by name
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${it.startingPoint.joinToString(" ")}&destination=${it.destination.joinToString(" ")}")
            }else{
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$slat,$slng&destination=$dlat,$dlng")
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

    }

    private fun setupRecyclerView(){
        mAdapter = UserPostsAdapter()
        // Swipe to delete
        val swipeCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val post = mAdapter.differ.currentList[viewHolder.adapterPosition]
                viewModel.deletePost(post)
                mAdapter.notifyDataSetChanged()

            }
        }

        rvActivePosts.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        // Attaching SwipeCallback to RecyclerView
        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvActivePosts)
    }

}