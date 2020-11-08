package com.astro.destishare.ui.homeFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.astro.destishare.ui.activities.MainActivity
import com.astro.destishare.R
import com.astro.destishare.adapters.HomeAdapter
import com.astro.destishare.models.firestore.postsmodels.PostsModel
import com.astro.destishare.notifications.NotificationData
import com.astro.destishare.notifications.PushNotification
import com.astro.destishare.ui.viewmodels.FirestoreViewModel
import com.astro.destishare.ui.activities.HomeActivity
import com.astro.destishare.util.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG = "HomeFragment"

    lateinit var auth : FirebaseAuth
    private val db = Firebase.firestore

    lateinit var viewModel : FirestoreViewModel
    lateinit var mAdapter : HomeAdapter
    lateinit var joinedPostsRef : CollectionReference
    

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        joinedPostsRef = db.collection("user-joined").document(auth.currentUser?.uid!!).collection("joinedPosts")

        setHasOptionsMenu(true)
        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        // ProgressBar
        viewModel.loadingState.observe(viewLifecycleOwner, Observer { isLoading->

            if (isLoading){
                progressBarHomeFragment.visibility = View.VISIBLE
            }else{
                progressBarHomeFragment.visibility = View.INVISIBLE
            }

        })

        // Getting JoinedPosts
        viewModel.getJoinedPosts(auth.currentUser?.uid!!)

        viewModel.joinedPostsLiveData
            .observe(viewLifecycleOwner, Observer {
                viewModel.getJoinedPostsIDs().observe(viewLifecycleOwner, Observer {
                    mAdapter.joinedIDs = it
                })
            })


        // Populating Recycler View
        viewModel.getAllPosts(auth.currentUser?.uid!!)

        viewModel.postsFromDB.observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)
            mAdapter.differFilter.submitList(it)

            if (it.isEmpty()){
                tvHomeFragmentNotify.visibility = View.VISIBLE
            }else{
                tvHomeFragmentNotify.visibility = View.INVISIBLE
            }

        })


        // OnJoinClick handler
        mAdapter.setOnJoinClickListener { thisPost->

            val senderName = auth.currentUser?.displayName
            val title = "$senderName wants to join you"
            val message = "${thisPost.startingPoint} -> ${thisPost.destination}"
            val senderUID = auth.currentUser?.uid!!
            val topic = "/topics/"+thisPost.userID

            // Send Notification to client
            // Adding this post to user-joined collection
            if (title.isNotEmpty() && message.isNotEmpty()){

                PushNotification(
                    NotificationData(title,message,senderUID, "-1",true),
                    topic
                ).also { pushNotification->
                    sendNotification(pushNotification, thisPost)
                }
            }
        }

        // View on map listener
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


        // Handling Menu
        toolbar_home.setOnMenuItemClickListener { menuItem->
            /*
                Logout user
            */
            if (menuItem.itemId == R.id.logout_home_menu){
                try {
                    (activity as HomeActivity).findViewById<ConstraintLayout>(R.id.clLoadingHomeActivity).visibility = View.VISIBLE
                    (activity as HomeActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.INVISIBLE
                    // "/topics/"+auth.currentUser?.uid
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(auth.currentUser?.uid!!).addOnCompleteListener { task->

                        if (task.isSuccessful){
                            auth.signOut() // Signing out from firebase
                            Log.d(TAG, "onViewCreated: Unsubscribing SUCCESS")
                            Intent(requireActivity(), MainActivity::class.java).also {
                                startActivity(it)
                            }
                        }else{
                            (activity as HomeActivity).findViewById<ConstraintLayout>(R.id.clLoadingHomeActivity).visibility = View.GONE
                            (activity as HomeActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE

                            Snackbar.make(
                                parentFragment?.view as View,
                                "Something went wrong!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.d(
                                TAG,
                                "onViewCreated: Unsubscribing FAILED -> ${task.exception?.message}"
                            )

                        }

                    }
                    
                    
                }catch (e: Exception){
                    e.printStackTrace()
                }

                /*
                    SearchView for all posts
                */
            }else if (menuItem.itemId == R.id.searchViewHomeFrag){

                val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView
                searchView.setOnQueryTextListener(object :
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        mAdapter.filter.filter(newText)
                        return false
                    }
                })

            }else if(menuItem.itemId == R.id.aboutUsMenu){

                findNavController().navigate(R.id.action_homeFragment_to_aboutUsFragment)

            }else if (menuItem.itemId == R.id.notificationMenu){

                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)

            }
            return@setOnMenuItemClickListener true
        }

    }

    @SuppressLint("LogNotTimber")
    private fun sendNotification(notification: PushNotification, post: PostsModel)= CoroutineScope(
        Dispatchers.IO
    ).launch {

        try {

            val response = RetrofitInstance.notificationAPI.postNotification(notification)

            if (response.isSuccessful){
//                Log.d(TAG, "sendNotification: RESPONSE -> {${Gson().toJson(respose)}}")
                Log.d(TAG, "sendNotification: Sent notification")

                /*
                Add this post to JoinedPosts Firebase sub-collections
                */
                joinedPostsRef.add(post)
                    .addOnSuccessListener {
                        Log.d(TAG, "sendNotification: Added this post to firestore")
                    }
                    .addOnFailureListener {
                        Log.d(
                            TAG,
                            "sendNotification: Failed to add this post to firestore -> ${it.message}"
                        )
                    }
            }else{
                Log.d(TAG, "sendNotification: ${response.errorBody()}")

            }

        }catch (e: Exception){
            e.printStackTrace()
            Log.d(TAG, "sendNotification: FAILED -> ${e.message}")

        }
    }

    private fun setupRecyclerView(){
        mAdapter = HomeAdapter()

        rvHomeFragment.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    
}